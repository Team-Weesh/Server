package com.example.weesh.core.auth.application;

import com.example.weesh.core.auth.application.jwt.TokenGenerator;
import com.example.weesh.core.auth.application.jwt.TokenResolver;
import com.example.weesh.core.auth.application.jwt.TokenStorage;
import com.example.weesh.core.auth.application.jwt.TokenValidator;
import com.example.weesh.core.auth.application.useCase.AuthUseCase;
import com.example.weesh.core.auth.exception.AuthErrorCode;
import com.example.weesh.core.auth.exception.AuthException;
import com.example.weesh.core.shared.PasswordValidator;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.data.jwt.JwtTokenResponse;
import com.example.weesh.web.auth.dto.AuthRequestDto;
import com.example.weesh.web.auth.dto.LogoutResponseDto;
import com.example.weesh.web.auth.dto.ProfileResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {
    private final AuthRepository authRepository;
    private final TokenGenerator tokenGenerator;
    private final TokenValidator tokenValidator;
    private final TokenStorage tokenStorage;
    private final PasswordValidator passwordValidator;

    @Override
    public JwtTokenResponse login(AuthRequestDto dto) {
        User user = authRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        passwordValidator.validate(dto.getPassword(), user.getPassword());
        return tokenGenerator.generateToken(user.getUsername(), user.getId());
    }

    @Override
    public ProfileResponseDto getProfileWithPortfolios(String username) {
        User user = authRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Map<String, Object> response = new HashMap<>();
        response.put("user", new com.example.weesh.web.user.dto.UserResponseDto(user));
        return new ProfileResponseDto(response);
    }

    @Override
    public String reissueAccessToken(String refreshToken) {
        tokenValidator.validateToken(refreshToken);
        String username = tokenValidator.getUsername(refreshToken);
        String storedRefreshToken = tokenStorage.getStoredRefreshToken(username);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN, "리프레시 토큰이 유효하지 않습니다.");
        }
        User user = authRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        JwtTokenResponse newTokens = tokenGenerator.generateToken(username, user.getId());
        tokenStorage.invalidateRefreshToken(username);
        tokenStorage.storeNewRefreshToken(username, newTokens.refreshToken(), 14 * 24 * 60 * 60 * 1000L); // 14일
        return newTokens.accessToken();
    }

    @Override
    public LogoutResponseDto logout(String accessToken) {
        tokenValidator.validateToken(accessToken);
        String username = tokenValidator.getUsername(accessToken);
        tokenStorage.invalidateRefreshToken(username);
        tokenStorage.blacklistAccessToken(accessToken);
        return new LogoutResponseDto("로그아웃 성공");
    }
}
