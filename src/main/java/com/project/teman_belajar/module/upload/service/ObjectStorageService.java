package com.project.teman_belajar.module.upload.service;

import com.project.teman_belajar.module.materials.exception.custom_exception.FileNotFoundException;
import com.project.teman_belajar.module.upload.dto.response.StorageUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final S3Presigner s3Presigner;

    private final S3Client s3Client;

    @Value("${supabase.s3.endpoint}")
    private String endpoint;

    @Value("${supabase.s3.bucket-name}")
    private String bucketName;

    public StorageUrlResponse generatePresignedUrl(String uniqueFileName, String contentType) {

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

        } catch (S3Exception e) {
            throw new RuntimeException("Gagal menghapus file dari storage: " + e.getMessage());
        }
    }
}
