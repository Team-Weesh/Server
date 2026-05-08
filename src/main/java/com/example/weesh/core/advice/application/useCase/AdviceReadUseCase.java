package com.example.weesh.core.advice.application.useCase;

import com.example.weesh.web.advice.dto.AdviceResponseDto;
import com.example.weesh.web.advice.dto.AdviceTimeResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AdviceReadUseCase {
    List<AdviceResponseDto> getAdvice();
    List<AdviceTimeResponseDto> getMyAdviceTimes(HttpServletRequest request);
}
