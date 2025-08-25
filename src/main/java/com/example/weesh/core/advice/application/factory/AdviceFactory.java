package com.example.weesh.core.advice.application.factory;

import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;

public interface AdviceFactory {
    Advice createAdvice(AdviceCreateRequestDto dto, Long userId);
}
