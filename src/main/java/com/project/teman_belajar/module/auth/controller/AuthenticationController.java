package com.project.teman_belajar.module.auth.controller;

import com.project.teman_belajar.module.auth.dto.request.*;
import com.project.teman_belajar.module.auth.dto.response.AuthenticationResponse;
import com.project.teman_belajar.module.auth.dto.response.DeleteRefreshTokenResponse;
import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.auth.dto.response.ValidOtpResponse;
import com.project.teman_belajar.module.auth.service.AuthenticationService;
import com.project.teman_belajar.module.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<SuccessResponse> changePassword(@RequestBody ChangePasswordRequest request){
        return ResponseEntity.ok(authenticationService.changePassword(request));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<SuccessResponse> sendOtp(@RequestBody SendOtpRequest request){
        return ResponseEntity.ok(authenticationService.sendOtpWithEmail(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ValidOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request){
        return ResponseEntity.ok(authenticationService.validateOtpOrThrow(request));
    }

    @PostMapping("/log-out")
    public ResponseEntity<DeleteRefreshTokenResponse> logout(@RequestBody LogoutRequest request) {
        return ResponseEntity.ok(refreshTokenService.deleteByUserId(request.id()));
    }
}
