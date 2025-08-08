package com.example.weesh.web.auth;

import com.example.weesh.core.auth.application.jwt.TokenResolver;
import com.example.weesh.core.auth.application.jwt.TokenStorage;
import com.example.weesh.core.auth.application.jwt.TokenValidator;
import com.example.weesh.core.auth.application.useCase.AuthUseCase;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.data.jwt.JwtTokenResponse;
import com.example.weesh.data.jwt.TokenServiceImpl;
import com.example.weesh.web.auth.dto.AuthRequestDto;
import com.example.weesh.web.auth.dto.LogoutResponseDto;
import com.example.weesh.web.auth.dto.ProfileResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthUseCase authUseCase;
    private final TokenResolver tokenResolver;
    private final TokenStorage tokenStorage;
    private final TokenValidator tokenValidator; // DIP 준수

    public AuthController(AuthUseCase authUseCase, TokenResolver tokenResolver, TokenStorage tokenStorage, TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator; // DIP 준수
        this.tokenResolver = tokenResolver; // DIP 준수
        this.authUseCase = authUseCase;
        this.tokenStorage = tokenStorage; // DIP 준수
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> loginWithValidation(@Valid @RequestBody AuthRequestDto requestLogin) {
        JwtTokenResponse response = authUseCase.login(requestLogin);
        return ResponseEntity
                .ok(ApiResponse
                        .success("로그인 성공", response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile(HttpServletRequest request) {
        String accessToken = tokenResolver.resolveToken(request);
        tokenValidator.validateToken(accessToken);
        ProfileResponseDto response = authUseCase.getProfileWithPortfolios(tokenValidator.getUsername(accessToken));
        return ResponseEntity
                .ok(ApiResponse
                        .success("프로필 조회 성공", response));

    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> reissue(HttpServletRequest request) {
        String refreshToken = tokenResolver.resolveRefreshToken(request);
        JwtTokenResponse response = new JwtTokenResponse(
                authUseCase.reissueAccessToken(refreshToken),
                tokenStorage.getStoredRefreshToken(tokenValidator.getUsername(refreshToken)),
                TokenServiceImpl.BEARER,
                TokenServiceImpl.ACCESS_TOKEN_VALID_TIME
        );
        return ResponseEntity
                .ok(ApiResponse
                        .success("토큰 재발급 성공", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponseDto>> logout(HttpServletRequest request) {
        String accessToken = tokenResolver.resolveToken(request); // resolveToken 메서드 추가 필요
        authUseCase.logout(accessToken);
        return ResponseEntity
                .ok(ApiResponse
                        .success("로그아웃 성공", null));
    }
}