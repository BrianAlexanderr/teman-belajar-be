package com.project.teman_belajar.module.folder.controller;

import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.folder.dto.request.FolderRequest;
import com.project.teman_belajar.module.folder.dto.request.RenameFolderRequest;
import com.project.teman_belajar.module.folder.dto.response.FolderCreateSuccessResponse;
import com.project.teman_belajar.module.folder.dto.response.UserFolderResponse;
import com.project.teman_belajar.module.folder.service.FoldersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
public class FoldersController {

    private final FoldersService foldersService;

    @GetMapping("/user")
    public ResponseEntity<List<UserFolderResponse>> getFolderByUser(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(
                foldersService.getUserFolders(user.getId())
        );
    }

    @PostMapping("/create")
    public ResponseEntity<FolderCreateSuccessResponse> createFolder(@RequestBody @Valid FolderRequest request){
        return ResponseEntity.ok(
                foldersService.createFolder(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserFolderResponse> getFolderById(@PathVariable UUID id){
        return ResponseEntity.ok(
                foldersService.findFolderById(id)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<String> renameFolder(
            @RequestBody RenameFolderRequest request
    ){
        foldersService.renameFolder(request);
        return ResponseEntity.ok("Nama folder berhasil diubah");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFolderById(@PathVariable UUID id){
        foldersService.deleteFolderById(id);
        return ResponseEntity.ok(
                "Folder berhasil dihapus"
        );
    }
}
