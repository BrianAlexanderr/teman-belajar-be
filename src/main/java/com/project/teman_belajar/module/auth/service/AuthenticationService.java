package com.project.teman_belajar.module.auth.service;

import com.project.teman_belajar.module.auth.dto.request.AuthenticationRequest;
import com.project.teman_belajar.module.auth.dto.request.RegisterRequest;
import com.project.teman_belajar.module.auth.dto.response.AuthenticationResponse;
import com.project.teman_belajar.module.auth.entities.Role;
import com.project.teman_belajar.module.auth.entities.Users;
import com.project.teman_belajar.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Users user = new Users();

        user.setName(request.firstName() + ' ' + request.lastName());
        user.setEmail(request.email());
        user.setPasswordHashed(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setSubscribe(false);
        user.setCreatedAt(new Date());

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
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

        String jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }
}
