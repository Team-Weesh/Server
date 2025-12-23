package com.example.weesh.core.advice.application;

import com.example.weesh.core.advice.application.factory.AdviceFactory;
import com.example.weesh.core.advice.application.useCase.*;
import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.advice.exception.DuplicateAdviceException;
import com.example.weesh.core.auth.application.token.TokenResolver;
import com.example.weesh.core.auth.application.token.TokenValidator;
import com.example.weesh.core.user.application.UserRepository;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;
import com.example.weesh.web.advice.dto.AdviceResponseDto;
import com.example.weesh.web.advice.dto.AdviceUpdateRequestDro;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdviceService implements AdviceCreateUseCase, AdviceReadUseCase, AdviceUpdateUseCase, AdviceApproveUseCase, AdviceDeleteUseCase {
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
        User user = userId != null ? userRepository.findById(userId) : null;
        validateAdviceRequest(dto, userId, user);
        validateDuplicateAdvice(dto);
        Advice advice = adviceFactory.createAdvice(dto, userId);
        Advice savedAdvice = adviceRepository.save(advice, user);
        return new AdviceResponseDto(savedAdvice);
    }

    @Override
    public List<AdviceResponseDto> getAdvice() {
        List<Advice> adviceList = adviceRepository.findAll();
        return adviceList.stream()
                .map(AdviceResponseDto::new)
                .toList();
    }

    @Override
    @Transactional
    public AdviceResponseDto approveAdvice(Long adviceId) {
        Advice advice = adviceRepository.findById(adviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + adviceId + "의 상담 예약을 찾을 수 없습니다."));

        advice.approve();
        Advice updatedAdvice = adviceRepository.save(advice);
        return new AdviceResponseDto(updatedAdvice);
    }

    @Override
    @Transactional
    public AdviceResponseDto updateAdvice(Long adviceId, AdviceUpdateRequestDro dto) {
        Advice advice = adviceRepository.findById(adviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + adviceId + "의 상담 예약을 찾을 수 없습니다."));

        advice.update(dto);
        Advice updatedAdvice = adviceRepository.save(advice);
        return new AdviceResponseDto(updatedAdvice);
    }

    @Override
    @Transactional
    public AdviceResponseDto deleteAdvice(Long adviceId) {
        Advice advice = adviceRepository.findById(adviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + adviceId + "의 상담 예약을 찾을 수 없습니다."));

        advice.delete();
        Advice updatedAdvice = adviceRepository.save(advice);
        return new AdviceResponseDto(updatedAdvice);
    }

    private Long getUserIdFromToken(String token) {
        try {
            tokenValidator.validateToken(token); // 토큰 유효성 검증
            return tokenValidator.parseToken(token).get("userId", Long.class); // userId 추출
        } catch (Exception e) {
            throw new IllegalArgumentException("유효한 사용자 ID를 토큰에서 추출할 수 없습니다.", e);
        }
    }

    private void validateAdviceRequest(AdviceCreateRequestDto dto, Long userId, User user) {
        if (userId != null) {
            if (dto.getStudentNumber() != null || dto.getFullName() != null) {
                throw new IllegalStateException("토큰 값이 있으면 학번 및 이름 필드엔 값이 없어야 합니다.");
            }
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

    private void validateDuplicateAdvice(AdviceCreateRequestDto dto) {
        if (adviceRepository.existsByDateAndTime(dto.getDesiredDate(), dto.getDesiredTime())) {
            throw new DuplicateAdviceException("이미 예약된 시간입니다. : " + dto.getDesiredDate() + "." + dto.getDesiredTime());
        }
    }
}
