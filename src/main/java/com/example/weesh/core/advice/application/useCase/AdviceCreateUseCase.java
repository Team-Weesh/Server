package com.example.weesh.core.advice.application.useCase;

import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;
import com.example.weesh.web.advice.dto.AdviceResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AdviceCreateUseCase {
    AdviceResponseDto createAdvice(AdviceCreateRequestDto dto, HttpServletRequest request);
}
