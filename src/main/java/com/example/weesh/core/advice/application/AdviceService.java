package com.example.weesh.core.advice.application;

import com.example.weesh.core.advice.application.factory.AdviceFactory;
import com.example.weesh.core.advice.application.useCase.*;
import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.advice.exception.DuplicateAdviceException;
import com.example.weesh.core.advice.exception.UnauthorizedUserException;
import com.example.weesh.core.advice.exception.UserNotFoundException;
import com.example.weesh.core.auth.application.token.TokenResolver;
import com.example.weesh.core.auth.application.token.TokenValidator;
import com.example.weesh.core.unavailableDate.application.UnavailableDateRepository;
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
    private final UnavailableDateRepository unavailableDateRepository;

    @Transactional
    @Override
    public AdviceResponseDto createAdvice(AdviceCreateRequestDto dto, HttpServletRequest request) {
        String token = tokenResolver.resolveToken(request); // 요청에서 토큰 추출

        if (token == null) {
            throw new UnauthorizedUserException("로그인이 필요합니다.");
        }

        Long userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new UserNotFoundException("존재하지 않는 유저입니다.");
        }

        validateUnavailableDate(dto.getDesiredDate());
        validateDuplicateAdvice(dto);
        Advice advice = adviceFactory.createAdvice(dto, userId, user.getStudentNumber(), user.getFullName());
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

    private void validateUnavailableDate(String desiredDate) {
        if (unavailableDateRepository.existsByDate(desiredDate)) {
            throw new IllegalArgumentException("해당 날짜는 상담이 불가능한 날짜입니다: " + desiredDate);
        }
    }

    private Long getUserIdFromToken(String token) {
        tokenValidator.validateToken(token);
        Long userId = tokenValidator.parseToken(token).get("userId", Long.class); // userId 추출
        if (userId == null) {
            throw new UnauthorizedUserException("유효한 사용자 Id를 토큰에서 추출할 수 없습니다.");
        }
        return userId;
    }

    private void validateDuplicateAdvice(AdviceCreateRequestDto dto) {
        if (adviceRepository.existsByDateAndTime(dto.getDesiredDate(), dto.getDesiredTime())) {
            throw new DuplicateAdviceException("이미 예약된 시간입니다. : " + dto.getDesiredDate() + "." + dto.getDesiredTime());
        }
    }
}
