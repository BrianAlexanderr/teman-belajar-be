package com.project.teman_belajar.module.auth.dto.response;

public record AuthenticationResponse(
  String userName,
  String token,
  String refreshToken
){}
