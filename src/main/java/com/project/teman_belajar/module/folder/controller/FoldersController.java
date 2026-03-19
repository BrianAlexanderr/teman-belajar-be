package com.project.teman_belajar.module.folder.controller;

import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.folder.dto.request.FolderRequest;
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
        return foldersService.getUserFolders(user.getId());
    }

    @PostMapping("/user")
    public ResponseEntity<FolderCreateSuccessResponse> createFolder(@RequestBody @Valid FolderRequest request){
        return foldersService.createFolder(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserFolderResponse> getFolderById(@PathVariable UUID id){
        return foldersService.findFolderById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolderById(@PathVariable UUID id){
        return foldersService.deleteFolderById(id);
    }
}
