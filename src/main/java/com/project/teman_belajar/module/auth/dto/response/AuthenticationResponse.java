package com.project.teman_belajar.module.auth.dto.response;

public record AuthenticationResponse(
  String token,
  String refreshToken
){}
