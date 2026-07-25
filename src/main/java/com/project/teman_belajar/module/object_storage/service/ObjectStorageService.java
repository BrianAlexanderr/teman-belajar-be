package com.project.teman_belajar.module.object_storage.service;

import com.project.teman_belajar.module.materials.exception.custom_exception.FileNotFoundException;
import com.project.teman_belajar.module.object_storage.dto.response.StorageUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectStorageService {

    private final S3Presigner s3Presigner;

    private final S3Client s3Client;

    @Value("${supabase.s3.endpoint}")
    private String endpoint;

    @Value("${supabase.s3.bucket-name}")
    private String bucketName;

    public StorageUrlResponse generatePresignedPutUrl(String uniqueFileName, String contentType) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return StorageUrlResponse.builder()
                .fileName(uniqueFileName)
                .url(presignedRequest.url().toString())
                .build();
    }

    public String generatePresignedGetUrl(String uniqueFileName) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public StorageUrlResponse generateViewUrl(String uniqueFileName, String fileName) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .build();

            s3Client.headObject(headObjectRequest);

            String baseUrl = endpoint.replace("/s3", "/object/public");

            return StorageUrlResponse.builder()
                    .fileName(fileName)
                    .url(baseUrl + "/" + bucketName + "/" + uniqueFileName)
                    .build();

        } catch (NoSuchKeyException e) {
            throw new FileNotFoundException("File tidak ditemukan di storage: " + fileName);
        } catch (S3Exception e) {
            throw new RuntimeException("Gagal menghubungi storage: " + e.getMessage());
        }
    }

    public void deleteFile(String uniqueFileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File {} berhasil dihapus", uniqueFileName);

        } catch (S3Exception e) {
            throw new RuntimeException("Gagal menghapus file dari storage: " + e.getMessage());
        }
    }

    public void deleteBulkFile(List<String> fileNameList) {
        if (fileNameList == null || fileNameList.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> keys = fileNameList.stream()
                .map(fileName -> ObjectIdentifier.builder().key(fileName).build())
                .toList();

        try {
            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(keys).build())
                    .build();

            s3Client.deleteObjects(deleteRequest);
            log.info("{} file berhasil dihapus secara batch dari S3", fileNameList.size());

        } catch (S3Exception e) {
            log.error("Gagal menghapus batch file dari S3: {}", e.getMessage());
            throw new RuntimeException("Gagal menghapus batch file");
        }
    }
}
