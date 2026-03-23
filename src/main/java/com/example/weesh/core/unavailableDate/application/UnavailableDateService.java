package com.example.weesh.core.unavailabledate.application;

import com.example.weesh.core.unavailabledate.application.factory.UnAvailableDateFactory;
import com.example.weesh.core.unavailabledate.application.useCase.*;
import com.example.weesh.core.unavailabledate.domain.UnAvailableDate;
import com.example.weesh.core.unavailabledate.exception.UnauthorizedUserException;
import com.example.weesh.core.auth.application.token.TokenResolver;
import com.example.weesh.core.auth.application.token.TokenValidator;
import com.example.weesh.core.user.application.UserRepository;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.web.unavailabledate.dto.UnAvailableDateCreateRequestDto;
import com.example.weesh.web.unavailabledate.dto.UnAvailableDateResponseDto;
import com.example.weesh.web.unavailabledate.dto.UnAvailableDateUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnAvailableDateService implements UnAvailableDateCreateUseCase, UnAvailableDateReadUseCase, UnAvailableDateUpdateUseCase, UnAvailableDateDeleteUseCase {
    private final UnAvailableDateRepository unAvailableDateRepository;
    private final UnAvailableDateFactory unAvailableDateFactory;
    private final TokenResolver tokenResolver;
    private final UserRepository userRepository;
    private final TokenValidator tokenValidator;

    @Transactional
    @Override
    public UnAvailableDateResponseDto createUnAvailableDate(UnAvailableDateCreateRequestDto dto, HttpServletRequest request) {
        String token = tokenResolver.resolveToken(request);

        if (token == null) {
            throw new UnauthorizedUserException("로그인이 필요합니다.");
        }

        Long userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId);

        if (user == null || !user.isAdmin()) {
            throw new UnauthorizedUserException("관리자 권한이 필요합니다.");
        }

        UnAvailableDate unAvailableDate = unAvailableDateFactory.createUnAvailableDate(dto);
        UnAvailableDate savedUnAvailableDate = unAvailableDateRepository.save(unAvailableDate);
        return new UnAvailableDateResponseDto(savedUnAvailableDate);
    }

    @Override
    public List<UnAvailableDateResponseDto> getUnAvailableDates() {
        List<UnAvailableDate> unAvailableDateList = unAvailableDateRepository.findAll();
        return unAvailableDateList.stream()
                .map(UnAvailableDateResponseDto::new)
                .toList();
    }

    @Transactional
    @Override
    public UnAvailableDateResponseDto activateUnAvailableDate(Long id, HttpServletRequest request) {
        validateAdmin(request);
        UnAvailableDate unAvailableDate = unAvailableDateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + id + "의 불가 날짜를 찾을 수 없습니다."));

        unAvailableDate.activate();
        UnAvailableDate updatedUnAvailableDate = unAvailableDateRepository.save(unAvailableDate);
        return new UnAvailableDateResponseDto(updatedUnAvailableDate);
    }

    @Transactional
    @Override
    public UnAvailableDateResponseDto deactivateUnAvailableDate(Long id, HttpServletRequest request) {
        validateAdmin(request);
        UnAvailableDate unAvailableDate = unAvailableDateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + id + "의 불가 날짜를 찾을 수 없습니다."));

        unAvailableDate.deactivate();
        UnAvailableDate updatedUnAvailableDate = unAvailableDateRepository.save(unAvailableDate);
        return new UnAvailableDateResponseDto(updatedUnAvailableDate);
    }

    @Transactional
    @Override
    public void deleteUnAvailableDate(Long id, HttpServletRequest request) {
        validateAdmin(request);
        UnAvailableDate unAvailableDate = unAvailableDateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + id + "의 불가 날짜를 찾을 수 없습니다."));

        unAvailableDateRepository.delete(unAvailableDate);
    }

    private Long getUserIdFromToken(String token) {
        tokenValidator.validateToken(token);
        Long userId = tokenValidator.parseToken(token).get("userId", Long.class);
        if (userId == null) {
            throw new UnauthorizedUserException("유효한 사용자 Id를 토큰에서 추출할 수 없습니다.");
        }
        return userId;
    }

    private void validateAdmin(HttpServletRequest request) {
        String token = tokenResolver.resolveToken(request);
        if (token == null) {
            throw new UnauthorizedUserException("로그인이 필요합니다.");
        }
        Long userId = getUserIdFromToken(token);
        User user = userRepository.findById(userId);
        if (user == null || !user.isAdmin()) {
            throw new UnauthorizedUserException("관리자 권한이 필요합니다.");
        }
    }
}
