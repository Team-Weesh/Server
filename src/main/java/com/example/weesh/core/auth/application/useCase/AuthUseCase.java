package com.example.weesh.core.auth.application.useCase;

import com.example.weesh.data.jwt.JwtTokenResponse;
import com.example.weesh.web.auth.dto.AuthRequestDto;
import com.example.weesh.web.auth.dto.LogoutResponseDto;
import com.example.weesh.web.auth.dto.ProfileResponseDto;

public interface AuthUseCase {
    JwtTokenResponse login(AuthRequestDto dto);
    ProfileResponseDto getProfileWithPortfolios(String username);
    String reissueAccessToken(String refreshToken);
    LogoutResponseDto logout(String accessToken);
}