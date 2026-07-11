package com.project.teman_belajar.module.upload;


import com.project.teman_belajar.module.materials.service.MaterialsService;
import com.project.teman_belajar.module.upload.enums.UploadStatunEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadCleanScheduler {

    private final MaterialsService materialsService;


    @Scheduled(fixedDelay = 300000)
    public void cleanupFailedUploads() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(15);

        try {
            int deletedCount = materialsService.deleteAllExpiredFile(
                    UploadStatunEnum.PENDING.getLabel(),
                    expirationTime
            );
            if(deletedCount > 0) {
                log.info("Berhasil menghapus {} file dengan status Pending", deletedCount);
            }
        } catch (Exception e) {
            log.error("Gagal menjalankan cleanup failed uploads", e);
        }

    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanDeletedFile() {
        materialsService.deleteBulkFile();
    }

}
