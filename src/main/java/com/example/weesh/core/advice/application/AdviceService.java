package com.example.weesh.core.advice.application;

import com.example.weesh.core.advice.application.factory.AdviceFactory;
import com.example.weesh.core.advice.application.useCase.AdviceCreateUseCase;
import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.auth.application.jwt.TokenResolver;
import com.example.weesh.core.auth.application.jwt.TokenValidator;
import com.example.weesh.core.user.application.UserRepository;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;
import com.example.weesh.web.advice.dto.AdviceResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdviceService implements AdviceCreateUseCase {
    private final AdviceRepository adviceRepository;
    private final AdviceFactory adviceFactory;
    private final TokenResolver tokenResolver;
    private final UserRepository userRepository;
    private final TokenValidator tokenValidator;

    @Transactional
    @Override
    public AdviceResponseDto createAdvice(AdviceCreateRequestDto dto, HttpServletRequest request) {
        String token = tokenResolver.resolveToken(request); // 요청에서 토큰 추출
        Long userId = token != null ? getUserIdFromToken(token) : null;
        validateAdviceRequest(dto, userId);
        Advice advice = adviceFactory.createAdvice(dto, userId);
        Advice savedAdvice = adviceRepository.save(advice);
        return new AdviceResponseDto(savedAdvice);
    }

    private Long getUserIdFromToken(String token) {
        try {
            tokenValidator.validateToken(token); // 토큰 유효성 검증
            return tokenValidator.parseToken(token).get("userId", Long.class); // userId 추출
        } catch (Exception e) {
            throw new IllegalArgumentException("유효한 사용자 ID를 토큰에서 추출할 수 없습니다.", e);
        }
    }

    private void validateAdviceRequest(AdviceCreateRequestDto dto, Long userId) {
        if (userId != null) {
            if (dto.getStudentNumber() != null || dto.getFullName() != null) {
                throw new IllegalStateException("토큰 값이 있으면 학번 및 이름 필드엔 값이 없어야 합니다.");
            }
            // 로그인 시 사용자 정보 자동 설정 (예시)
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalStateException("User not found");
            }
            dto.setStudentNumber(user.getStudentNumber());
            dto.setFullName(user.getFullName());
        } else {
            if (dto.getStudentNumber() == null || dto.getFullName() == null) {
                throw new IllegalStateException("비로그인 시 학번과 이름은 필수입니다.");
            }
        }
    }
}
