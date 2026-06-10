package com.project.teman_belajar.module.folder.service;

import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.folder.dto.request.FolderRequest;
import com.project.teman_belajar.module.folder.dto.request.RenameFolderRequest;
import com.project.teman_belajar.module.folder.dto.response.FolderCreateSuccessResponse;
import com.project.teman_belajar.module.folder.dto.response.UserFolderResponse;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.FolderNotFoundException;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.SameFolderNameException;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.UserNotFoundException;
import com.project.teman_belajar.module.folder.repository.FoldersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FoldersService {
    private final FoldersRepository foldersRepository;

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

    public void renameFolder(RenameFolderRequest request){
        Folders folder = foldersRepository.findById(request.id())
                .orElseThrow(() -> new FolderNotFoundException("Folder tidak ditemukan"));

        if(request.newName().equals(folder.getName())){
            throw new SameFolderNameException("Folder harus memiliki nama yang berbeda!");
        }

        folder.setName(request.newName());

        foldersRepository.save(folder);
    }

    public UserFolderResponse findFolderById(UUID id){
        Optional<Folders> folders = foldersRepository.findById(id);

        if(folders.isEmpty()) throw new FolderNotFoundException("Folder tidak ditemukan");

        return new UserFolderResponse(
                folders.get().getId(),
                folders.get().getName()
        );
    }

    public void deleteFolderById(UUID folderId){
        Optional<Folders> folder = foldersRepository.findById(folderId);

        if(folder.isEmpty()) throw new FolderNotFoundException("Folder tidak ditemukan");

        foldersRepository.delete(folder.get());
    }
}
