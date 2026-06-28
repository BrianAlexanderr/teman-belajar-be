package com.project.teman_belajar.module.materials.service;

import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.FolderNotFoundException;
import com.project.teman_belajar.module.folder.repository.FoldersRepository;
import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.materials.exception.custom_exception.FileNotFoundException;
import com.project.teman_belajar.module.materials.exception.custom_exception.FileTypeNotAllowedException;
import com.project.teman_belajar.module.materials.repository.MaterialsRepository;
import com.project.teman_belajar.module.upload.service.ObjectStorageService;
import com.project.teman_belajar.module.upload.dto.request.UploadFileRequest;
import com.project.teman_belajar.module.upload.dto.response.StorageUrlResponse;
import com.project.teman_belajar.module.upload.enums.AllowedMaterialFileType;
import com.project.teman_belajar.module.upload.enums.UploadStatunEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialsService {

    private final MaterialsRepository materialsRepository;
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

    public List<Materials> getMaterialByStatusAndCreatedAt(String status, LocalDateTime createdAt){
        return materialsRepository.findByStatusAndCreatedAtBefore(
                status,
                createdAt
        );
    }

    public StorageUrlResponse uploadFile(UploadFileRequest uploadFileRequest) {
        String fileName = uploadFileRequest.fileName();
        String fileType = uploadFileRequest.fileType();

        if(!AllowedMaterialFileType.isValidContentType(fileType)) {
            throw new FileTypeNotAllowedException("Tipe file tidak valid");
        }

        Folders folder = foldersRepository.findById(
                UUID.fromString(uploadFileRequest.folderId())
        ).orElseThrow(() -> new FolderNotFoundException("Folder tidak ditemukan"));

        Materials materials = buildMaterial(fileName, fileType, folder);

        String materialId = materialsRepository.saveAndFlush(materials).getId().toString();

        return objectStorageService.generatePresignedUrl(
                materialId,
                fileType
        );
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

    public void deleteAllExpiredFile(List<Materials> expiredPendingFiles) {
        materialsRepository.deleteAll(expiredPendingFiles);
    }

    public StorageUrlResponse getViewUrl(String id, String fileName) {
        return objectStorageService.generateViewUrl(
                id,
                fileName
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
}
