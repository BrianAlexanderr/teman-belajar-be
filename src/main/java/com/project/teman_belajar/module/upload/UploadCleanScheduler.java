package com.project.teman_belajar.module.upload;

import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.materials.service.MaterialsService;
import com.project.teman_belajar.module.upload.enums.UploadStatunEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadCleanScheduler {

    private final MaterialsService materialsService;

    @Scheduled(fixedRate = 900000)
    public void cleanupFailedUploads() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(15);

        List<Materials> expiredPendingFiles = materialsService.getMaterialByStatusAndCreatedAt(
                        UploadStatunEnum.PENDING.getLabel(),
                        expirationTime
                );

        if (!expiredPendingFiles.isEmpty()) {
            materialsService.deleteAllExpiredFile(expiredPendingFiles);
            log.info("Menghapus {} metadata upload yang gagal.", expiredPendingFiles.size());
        }
    }

}
