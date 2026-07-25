package com.project.teman_belajar.module.summarize.service;

import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.FolderNotFoundException;
import com.project.teman_belajar.module.folder.repository.FoldersRepository;
import com.project.teman_belajar.module.llm.dto.response.DocumentSummaryResponse;
import com.project.teman_belajar.module.llm.service.GeminiDocumentService;
import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.materials.exception.custom_exception.FileNotFoundException;
import com.project.teman_belajar.module.materials.repository.MaterialsRepository;
import com.project.teman_belajar.module.object_storage.service.ObjectStorageService;
import com.project.teman_belajar.module.summarize.dto.request.SummaryRequest;
import com.project.teman_belajar.module.summarize.dto.response.SummaryDetailResponse;
import com.project.teman_belajar.module.summarize.dto.response.SummaryPreviewResponse;
import com.project.teman_belajar.module.summarize.entities.Summary;
import com.project.teman_belajar.module.summarize.exception.custom_exception.DocumentInvalidException;
import com.project.teman_belajar.module.summarize.exception.custom_exception.SummaryNotFoundException;
import com.project.teman_belajar.module.summarize.repository.SummaryRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SummaryService {

    private final GeminiDocumentService documentAnalyzer;
    private final ObjectStorageService storageService;
    private final MaterialsRepository materialsRepository;
    private final SummaryRepository summaryRepository;
    private final FoldersRepository foldersRepository;

    @Transactional
    public SuccessResponse createCombinedSummary(SummaryRequest request) {
        log.info("Starting combined AI synthesis for {} files inside folder ID: {}",
                request.materialIds().size(),
                request.folderId()
        );

        Folders folder = foldersRepository.findById(request.folderId())
                .orElseThrow(() -> new FolderNotFoundException("Folder not found with ID: " + request.folderId()));

        List<Content> combinedPayload = combinePayload(request.materialIds());

        DocumentSummaryResponse aiResponse = documentAnalyzer.summarizeMultipleFiles(combinedPayload);

        validateAiResponse(aiResponse);

        saveAndReturnSummary(aiResponse, folder);

        log.info("Berhasil Membuat dan menyimpan Summary!");

        return new SuccessResponse(
                "Berhasil Membuat Summary",
                new Date()
        );
    }

    public SummaryDetailResponse getDetailSummary(UUID summaryId) {
        Summary summary = getSummaryById(summaryId);
        return SummaryDetailResponse.builder()
                .id(summary.getId())
                .title(summary.getTitle())
                .keyPoint(summary.getKeyPoints())
                .content(summary.getContent())
                .build();
    }

    @Transactional
    public SuccessResponse deleteSummary(UUID summaryId) {
        if (!summaryRepository.existsById(summaryId)) {
            throw new SummaryNotFoundException("Summary not found with ID: " + summaryId);
        }
        summaryRepository.deleteById(summaryId);
        return new SuccessResponse(
                "Berhasil Menghapus Summary",
                new Date()
        );
    }

    private Summary getSummaryById(UUID summaryId) {
        return summaryRepository.findById(summaryId)
                .orElseThrow(() -> new SummaryNotFoundException("Summary not found with ID: " + summaryId));
    }

    public List<SummaryPreviewResponse> getListSummary(UUID folderId) {
        List<Summary> summaries = summaryRepository.findSummaryByFoldersId(folderId);
        return buildListSummaryPreviewResponse(summaries);
    }

    private List<SummaryPreviewResponse> buildListSummaryPreviewResponse(List<Summary> summaries) {
        return summaries.stream()
                .map(summary -> SummaryPreviewResponse.builder()
                        .id(summary.getId())
                        .title(summary.getTitle())
                        .preview(summary.getPreview())
                        .build()
                )
                .toList();
    }

    private void saveAndReturnSummary(DocumentSummaryResponse response, Folders folder) {
        Summary summary = new Summary();
        summary.setTitle(response.title());
        summary.setPreview(response.preview());
        summary.setKeyPoints(response.keyPoints());
        summary.setContent(response.content());
        summary.setFolders(folder);

        summaryRepository.save(summary);
    }

    private List<Content> combinePayload(List<UUID> materialIds) {
        List<Content> combinedPayload = new ArrayList<>();

        for (UUID materialId : materialIds) {
            try {
                Materials materials = materialsRepository.findById(materialId)
                        .orElseThrow(() -> new FileNotFoundException("File tidak ditemukan: " + materialId));

                String preSignedUrl = storageService.generatePresignedGetUrl(materialId.toString());

                Content mediaPart = resolveLangChainContent(preSignedUrl, materials.getType());
                combinedPayload.add(mediaPart);

            } catch (Exception e) {
                throw new DocumentInvalidException(
                        "Terdapat dokumen yang tidak valid!"
                );
            }
        }

        if (combinedPayload.isEmpty()) {
            throw new DocumentInvalidException(
                    "Tidak ada dokumen yang valid!"
            );
        }

        return combinedPayload;
    }

    private Content resolveLangChainContent(String url, String mimeType) {
        if (url == null || mimeType == null) {
            throw new DocumentInvalidException("URL and MimeType tidak boleh kosong");
        }

        String lowerMime = mimeType.toLowerCase();

        try {
            URLConnection connection = URI.create(url).toURL().openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            try (InputStream inputStream = connection.getInputStream()) {

                if (lowerMime.contains("image") || lowerMime.contains("png") || lowerMime.contains("jpg") || lowerMime.contains("jpeg")) {

                    byte[] imageBytes = inputStream.readAllBytes();

                    String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

                    return ImageContent.from(base64Image, mimeType);
                }
                else {

                    DocumentParser parser = new ApacheTikaDocumentParser();
                    Document document = parser.parse(inputStream);

                    String extractedText = document.text();

                    if (extractedText == null || extractedText.trim().isEmpty()) {
                        throw new DocumentInvalidException(
                                "Dokumen tidak berisi teks yang dapat dibaca. Pastikan file bukan sekadar foto/scan tanpa teks."
                        );
                    }

                    String formattedText = String.format("""
                            --- MULAI DOKUMEN (Tipe: %s) ---
                            %s
                            --- AKHIR DOKUMEN ---
                            """, mimeType, extractedText);

                    return TextContent.from(formattedText);
                }
            }
        } catch (Exception e) {
            throw new DocumentInvalidException("Gagal memproses dokumen dari Supabase storage: " + e.getMessage());
        }
    }

    private void validateAiResponse(DocumentSummaryResponse response) {
        if (response == null) {
            throw new DocumentInvalidException("AI menghasilkan respons kosong. Gagal memproses rangkuman.");
        }

        if (response.title() != null && response.title().equalsIgnoreCase("ERROR_UNREADABLE")) {
            throw new DocumentInvalidException("Dokumen tidak berisi materi belajar yang dapat dibaca oleh AI. Pastikan file tidak rusak atau kosong.");
        }

        if (response.content() == null || response.content().trim().length() < 50) {
            throw new DocumentInvalidException("Gagal menghasilkan rangkuman yang lengkap. Silakan coba unggah ulang materi Anda.");
        }
    }
}