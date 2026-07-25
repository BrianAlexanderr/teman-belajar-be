package com.project.teman_belajar.module.folder.service;

import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.folder.dto.request.FolderRequest;
import com.project.teman_belajar.module.folder.dto.request.RenameFolderRequest;
import com.project.teman_belajar.module.folder.dto.response.FolderCreateSuccessResponse;
import com.project.teman_belajar.module.materials.dto.response.MaterialDetailResponse;
import com.project.teman_belajar.module.folder.dto.response.UserFolderResponse;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.FolderNotFoundException;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.SameFolderNameException;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.UserNotFoundException;
import com.project.teman_belajar.module.folder.repository.FoldersRepository;
import com.project.teman_belajar.module.materials.entities.Materials;
import com.project.teman_belajar.module.materials.repository.DeletedMaterialRepository;
import com.project.teman_belajar.module.materials.repository.MaterialsRepository;
import com.project.teman_belajar.module.object_storage.enums.UploadStatunEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FoldersService {

    private final FoldersRepository foldersRepository;
    private final MaterialsRepository materialsRepository;
    private final DeletedMaterialRepository deletedMaterialRepository;

    public List<UserFolderResponse> getUserFolders(UUID id){
        Optional<List<Folders>> userFolder =  foldersRepository.findByUserId(id);
        if(userFolder.isEmpty()) throw new UserNotFoundException("Pengguna tidak ditemukan");

        List<UserFolderResponse> responses = new ArrayList<>();

        for(Folders folder: userFolder.get()){
            UserFolderResponse response = new UserFolderResponse(
                folder.getId(),
                folder.getName()
            );
            responses.add(response);
        }

        return responses;
    }

    public FolderCreateSuccessResponse createFolder(FolderRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Users user = (Users) Objects.requireNonNull(authentication).getPrincipal();

        Optional<Folders> folders = foldersRepository.findByNameAndUserId(request.name(), Objects.requireNonNull(user).getId());

        if(folders.isPresent()) throw new SameFolderNameException("Tidak bisa membuat folder dengan nama yang sama");

        Folders folder = new Folders();
        folder.setName(request.name());
        folder.setUser(user);

        foldersRepository.save(folder);

        return new FolderCreateSuccessResponse(
                "Success Created Folder",
                LocalDateTime.now().toString()
        );
    }

    public Folders findFolderByIdOrThrow(UUID id) {
        return foldersRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException("Folder id tidak ditemukan"));
    }

    public void renameFolder(RenameFolderRequest request){
        Folders folder = findFolderByIdOrThrow(request.id());

        if(request.newName().equals(folder.getName())){
            throw new SameFolderNameException("Folder harus memiliki nama yang berbeda!");
        }

        folder.setName(request.newName());

        foldersRepository.save(folder);
    }

    public UserFolderResponse findFolderById(UUID id){
        Folders folders = findFolderByIdOrThrow(id);

        return new UserFolderResponse(
                folders.getId(),
                folders.getName()
        );
    }

    private void listDeletedMaterial(List<Materials> materialsList) {
        for (Materials materials : materialsList) {
            deletedMaterialRepository.insertDeletedMaterial(materials.getId());
        }
    }

    @Transactional
    public void deleteFolderById(UUID folderId){
        Folders folder = findFolderByIdOrThrow(folderId);

        List<Materials> existingMaterial = materialsRepository.findByFolders_IdAndStatus(
                folderId,
                UploadStatunEnum.SUCCESS.getLabel()
        );

        if(!existingMaterial.isEmpty()) {
            listDeletedMaterial(existingMaterial);
        }

        foldersRepository.delete(folder);
    }

    private List<MaterialDetailResponse> transformToResponse(List<Materials> materialsList) {
        return materialsList.stream()
                .map(m ->
                        MaterialDetailResponse.builder()
                        .fileId(String.valueOf(m.getId()))
                        .fileName(m.getName())
                        .fileType(m.getType()).build()
                )
                .toList();
    }

    public List<MaterialDetailResponse> getMaterialFromFolder(String id) {
        List<Materials> materials = materialsRepository.findByFolders_IdAndStatus(
                UUID.fromString(id),
                UploadStatunEnum.SUCCESS.getLabel()
        );

        return transformToResponse(
                materials
        );
    }
}
