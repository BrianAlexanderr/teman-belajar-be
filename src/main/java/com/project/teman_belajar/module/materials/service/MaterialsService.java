package com.project.teman_belajar.module.materials.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.project.teman_belajar.common.global_exception.exception.ResourceNotFoundException;
import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.FolderNotFoundException;
import com.project.teman_belajar.module.folder.repository.FoldersRepository;
import com.project.teman_belajar.module.materials.dto.request.RenameMaterialRequest;
import com.project.teman_belajar.module.materials.dto.request.UploadImageRequest;
import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.materials.exception.custom_exception.FileNotFoundException;
import com.project.teman_belajar.module.materials.exception.custom_exception.FileTypeNotAllowedException;
import com.project.teman_belajar.module.materials.repository.DeletedMaterialRepository;
import com.project.teman_belajar.module.materials.repository.MaterialsRepository;
import com.project.teman_belajar.module.object_storage.service.ObjectStorageService;
import com.project.teman_belajar.module.object_storage.dto.request.UploadFileRequest;
import com.project.teman_belajar.module.object_storage.dto.response.StorageUrlResponse;
import com.project.teman_belajar.module.object_storage.enums.AllowedMaterialFileType;
import com.project.teman_belajar.module.object_storage.enums.UploadStatunEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialsService {

    private final MaterialsRepository materialsRepository;
    private final DeletedMaterialRepository deletedMaterialRepository;
    private final FoldersRepository foldersRepository;
    private final ObjectStorageService objectStorageService;

    private Materials buildMaterial(String fileName, String fileType, Folders folders){
        return Materials.builder()
                .name(fileName)
                .type(fileType)
                .status(UploadStatunEnum.PENDING.getLabel())
                .folders(folders)
                .build();
    }

    private void verifyMaterialOwnership(UUID materialId, UUID userId) {
        UUID ownerId = materialsRepository.findOwnerIdByMaterialId(
                materialId
        ).orElseThrow(() -> new ResourceNotFoundException("Owner Materi tidak ditemukan!"));

        if(!ownerId.equals(userId)) {
            throw new AccessDeniedException("Anda tidak memiliki hak akses materi ini!");
        }
    }

    public StorageUrlResponse uploadFile(UploadFileRequest request) {
        if (!AllowedMaterialFileType.isValidContentType(request.fileType())) {
            throw new FileTypeNotAllowedException("Tipe dokumen tidak valid");
        }

        Materials material = prepareAndSaveMaterial(request.fileName(), request.fileType(), request.folderId());

        return objectStorageService.generatePresignedPutUrl(
                material.getId().toString(),
                request.fileType()
        );
    }

    public SuccessResponse processTextAndUpload(UploadImageRequest request) {
        if (!AllowedMaterialFileType.isValidImageType(request.fileType())) {
            throw new FileTypeNotAllowedException("Tipe gambar tidak valid");
        }

        String pdfFileName = request.fileName().replaceAll("\\.[^.]+$", "") + ".pdf";
        String pdfContentType = "application/pdf";

        Materials material = prepareAndSaveMaterial(pdfFileName, pdfContentType, request.folderId());
        String materialId = material.getId().toString();

        try {
            byte[] processedPdfBytes = createPdf(request.extractedText());

            objectStorageService.uploadFileBytes(processedPdfBytes, materialId, pdfContentType);

            setUploadStatusSuccess(materialId);

            return new SuccessResponse(
                    "Berhasil Menyimpan Text dalam Gambar menjadi Dokumen!",
                    new Date()
            );
        } catch (Exception e) {
            throw new RuntimeException("Gagal memproses dan menyimpan dokumen: " + e.getMessage());
        }
    }

    private Materials prepareAndSaveMaterial(String fileName, String fileType, String folderId) {
        Folders folder = foldersRepository.findById(UUID.fromString(folderId))
                .orElseThrow(() -> new FolderNotFoundException("Folder tidak ditemukan"));

        Materials material = buildMaterial(fileName, fileType, folder);

        return materialsRepository.saveAndFlush(material);
    }

    public StorageUrlResponse downloadFile(UUID materialId, UUID userId) {

        verifyMaterialOwnership(materialId, userId);

        String presignedUrl = objectStorageService.generatePresignedGetUrl(
                materialId.toString()
        );

        return StorageUrlResponse.builder()
                .url(presignedUrl)
                .build();
    }

    public void setUploadStatusSuccess(String materialId) {
        UUID id =  UUID.fromString(materialId);

        Materials materials = materialsRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(
                        "Material tidak ditemukan"
                ));

        materials.setStatus(UploadStatunEnum.SUCCESS.getLabel());
        materialsRepository.saveAndFlush(materials);
    }

    @Transactional
    public int deleteAllExpiredFile(String status, LocalDateTime createdAt) {
        return materialsRepository.deleteByStatusAndCreatedAtBefore(
                status,
                createdAt
        );
    }

    public StorageUrlResponse getViewUrl(String id, String fileName, UUID userId) {

        Materials material = materialsRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new FileNotFoundException("Material tidak ditemukan"));

        verifyMaterialOwnership(material.getId(), userId);

        return objectStorageService.generateViewUrl(
                id,
                fileName
        );
    }

    public SuccessResponse renameMaterial(RenameMaterialRequest request) {
        Materials existingMaterial = materialsRepository.findById(
                UUID.fromString(request.id())
        ).orElseThrow(() -> new FileNotFoundException("File tidak ditemukan"));

        existingMaterial.setName(request.newName());

        materialsRepository.save(existingMaterial);

        return new SuccessResponse(
                "Berhasil mengubah nama file",
                new Date()
        );
    }

    @Transactional
    public SuccessResponse deleteFileById(String id) {
        Materials material = materialsRepository.findById(
                UUID.fromString(id)
        ).orElseThrow(() -> new FileNotFoundException("Material tidak ditemukan"));

        objectStorageService.deleteFile(id);

        materialsRepository.delete(material);

        return new SuccessResponse(
                "Berhasil Menghapus Material",
                new Date()
        );
    }

    public void deleteBulkFile() {

        List<String> deletedMaterials = deletedMaterialRepository.findIdsWithLimit();

        try {
            objectStorageService.deleteBulkFile(deletedMaterials);

            List<UUID> uuidList = deletedMaterials.stream()
                    .map(UUID::fromString)
                    .toList();

            deletedMaterialRepository.deleteAllById(uuidList);

            log.info("Berhasil membersihkan {} data dari deleted_materials", uuidList.size());

        } catch (Exception e) {
            log.error("Proses pembersihan file tertunda: {}", e.getMessage());
        }

    }

    private byte[] createPdf(String extractedText) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            Document document = new Document();

            PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();

            document.add(new Paragraph(extractedText));

            document.close();

            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat atau mengupload PDF: " + e.getMessage());
        }
    }
}
