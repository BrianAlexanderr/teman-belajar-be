package com.project.teman_belajar.module.folder.service;

import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.folder.dto.request.FolderRequest;
import com.project.teman_belajar.module.folder.dto.response.FolderCreateSuccessResponse;
import com.project.teman_belajar.module.folder.dto.response.UserFolderResponse;
import com.project.teman_belajar.module.folder.entities.Folders;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.FolderNotFoundException;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.SameFolderNameException;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.UserNotFoundException;
import com.project.teman_belajar.module.folder.repository.FoldersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FoldersService {
    private final FoldersRepository foldersRepository;

    public ResponseEntity<List<UserFolderResponse>> getUserFolders(UUID id){
        Optional<List<Folders>> userFolder =  foldersRepository.findByUserId(id);
        if(userFolder.isEmpty()) throw new UserNotFoundException("User not found!");

        List<UserFolderResponse> responses = new ArrayList<>();

        for(Folders folder: userFolder.get()){
            UserFolderResponse response = new UserFolderResponse(
                folder.getUser().getId(),
                folder.getName()
            );
            responses.add(response);
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    public ResponseEntity<FolderCreateSuccessResponse> createFolder(FolderRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Users user = (Users) Objects.requireNonNull(authentication).getPrincipal();

        Optional<Folders> folders = foldersRepository.findByNameAndUserId(request.name(), Objects.requireNonNull(user).getId());

        if(folders.isPresent()) throw new SameFolderNameException("Can't create folder with the same name!");

        Folders folder = new Folders();
        folder.setName(request.name());
        folder.setUser(user);
        folder.setCreatedAt(new Date());

        Folders savedFolder = foldersRepository.save(folder);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(savedFolder.getId());

        FolderCreateSuccessResponse response = new FolderCreateSuccessResponse(
                "Success Created Folder",
                LocalDateTime.now().toString()
        );

        return ResponseEntity.created(location).body(response);
    }

    public ResponseEntity<UserFolderResponse> findFolderById(UUID id){
        Optional<Folders> folders = foldersRepository.findById(id);

        if(folders.isEmpty()) throw new FolderNotFoundException("Folder of id " + id + " not found!");

        UserFolderResponse response = new UserFolderResponse(
                folders.get().getId(),
                folders.get().getName()
        );

        return ResponseEntity.ok(response);
    }
}
