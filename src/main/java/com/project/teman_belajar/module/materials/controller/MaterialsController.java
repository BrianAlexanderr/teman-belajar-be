package com.project.teman_belajar.module.materials.controller;

import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.materials.dto.request.RenameMaterialRequest;
import com.project.teman_belajar.module.materials.dto.request.SetStatusRequest;
import com.project.teman_belajar.module.materials.service.MaterialsService;
import com.project.teman_belajar.module.upload.dto.request.UploadFileRequest;
import com.project.teman_belajar.module.upload.dto.response.StorageUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/materials")
@RequiredArgsConstructor
public class MaterialsController {

    private final MaterialsService materialsService;

    @PostMapping("upload")
    public ResponseEntity<StorageUrlResponse> getMaterialsPresignedUrl(
            @RequestBody UploadFileRequest uploadFileRequest
    ){
        return ResponseEntity.ok(
                materialsService.uploadFile(
                        uploadFileRequest
                )
        );
    }

    @PostMapping("upload/success")
    public ResponseEntity<String> uploadSuccess(@RequestBody SetStatusRequest setStatusRequest) {
        materialsService.setUploadStatusSuccess(setStatusRequest.materialId());
        return ResponseEntity.ok(
                "Upload Berhasil"
        );
    }

    @GetMapping("info")
    public ResponseEntity<StorageUrlResponse> getMaterialsInfo(
            @RequestParam String id,
            @RequestParam String fileName
    ) {
        return ResponseEntity.ok(
                materialsService.getViewUrl(
                        id,
                        fileName
                )
        );
    }

    @PutMapping("rename")
    public ResponseEntity<SuccessResponse> renameMaterials(
            @RequestBody RenameMaterialRequest request
    ) {
        return ResponseEntity.ok(
                materialsService.renameMaterial(
                        request
                )
        );
    }

    @DeleteMapping("delete")
    public ResponseEntity<SuccessResponse> deleteMaterials(
            @RequestParam String id
    ) {
        return ResponseEntity.ok(
                materialsService.deleteFileById(id)
        );
    }

}
