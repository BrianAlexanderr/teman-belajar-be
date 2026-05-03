package com.project.teman_belajar.module.auth.service;

import com.project.teman_belajar.module.auth.dto.request.AuthenticationRequest;
import com.project.teman_belajar.module.auth.dto.request.ChangePasswordRequest;
import com.project.teman_belajar.module.auth.dto.request.RegisterRequest;
import com.project.teman_belajar.module.auth.dto.response.AuthenticationResponse;
import com.project.teman_belajar.module.auth.dto.response.SuccessResponse;
import com.project.teman_belajar.module.auth.entities.RefreshToken;
import com.project.teman_belajar.module.auth.entities.Role;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.auth.exception.custom_exception.DuplicateEmailException;
import com.project.teman_belajar.module.auth.exception.custom_exception.OtpNotValidException;
import com.project.teman_belajar.module.auth.exception.custom_exception.SamePasswordException;
import com.project.teman_belajar.module.auth.repository.UserRepository;
import com.project.teman_belajar.module.email.dto.request.SendEmailRequest;
import com.project.teman_belajar.module.email.service.EmailService;
import com.project.teman_belajar.module.folder.exception.custom_exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;


    private boolean isExist(String email){
        Optional<Users> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    public SuccessResponse register(RegisterRequest request) {

        if(isExist(request.email())){
            throw  new DuplicateEmailException("Email yang digunakan telah terdaftar!");
        }

        Users user = new Users();

        user.setName(request.firstName() + ' ' + request.lastName());
        user.setEmail(request.email());
        user.setPasswordHashed(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setSubscribe(false);

        userRepository.save(user);

        refreshTokenService.createRefreshToken(user.getId());

        return new SuccessResponse("Success", new Date());
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                    request.password()
            )
        );

        Users user = userRepository.findByEmail(request.email())
                .orElseThrow();

        String jwtToken = jwtService.generateAccessToken(user);

        RefreshToken refreshToken = refreshTokenService.getOrCreateRefreshToken(user.getId());

        return new AuthenticationResponse(jwtToken,  refreshToken.getToken());
    }

    public SuccessResponse sendOtpWithEmail(String email){

        String otp = otpService.generateAndStoreOtp(email);

        SendEmailRequest request = new SendEmailRequest(
                email,
                "test",
                "test",
                otp
        );

        try{
            emailService.sendOtpEmail(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new SuccessResponse(
                "Success Send Email",
                new Date()
        );
    }

    private void validateOtpOrThrow(String email, String otp){
        if(!otpService.validOtp(email, otp)){
            throw new OtpNotValidException("Otp yang digunakan tidak valid!");
        }
    }

    private void validateNewPassword(String rawNewPassword, String hashedOldPassword){
        if(passwordEncoder.matches(rawNewPassword, hashedOldPassword)){
            throw new SamePasswordException("New Password cannot be the same as the Old Password!");
        }
    }

    public SuccessResponse changePassword(ChangePasswordRequest request){
        String email = request.email();

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        validateOtpOrThrow(email, request.otp());

        validateNewPassword(request.newPassword(), user.getPassword());

        String hashedNewPassword = passwordEncoder.encode(request.newPassword());

        user.setPasswordHashed(hashedNewPassword);

        userRepository.save(user);

        otpService.deleteOtp(email);

        return new SuccessResponse("Success Change Password", new Date());
    }
}
