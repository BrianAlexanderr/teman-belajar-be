package com.project.teman_belajar.module.auth.controller;

import com.project.teman_belajar.module.auth.dto.request.UpdateUserProfileReuqest;
import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.auth.dto.response.UserProfileResponse;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(
                userService.getUserProfile(user.getId())
        );
    }

    @PutMapping("update")
    public ResponseEntity<SuccessResponse> updateUserProfile(
            @RequestBody UpdateUserProfileReuqest request,
            @AuthenticationPrincipal Users user
    ) {
        return ResponseEntity.ok(
                userService.updateUserProfile(request, user)
        );
    }

}
