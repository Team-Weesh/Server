package com.example.weesh.web.advice;

import com.example.weesh.core.advice.application.AdviceService;
import com.example.weesh.core.advice.application.useCase.AdviceCreateUseCase;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;
import com.example.weesh.web.advice.dto.AdviceResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advice")
@RequiredArgsConstructor
public class AdviceController {
    private final AdviceCreateUseCase adviceCreateUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<AdviceResponseDto>> createAdvice(
            @Valid @RequestBody AdviceCreateRequestDto dto,
            HttpServletRequest request) {
        AdviceResponseDto response = adviceCreateUseCase.createAdvice(dto, request);

        LoggingUtil.info("New advice created for student number: {}", dto.getFullName());
        return ResponseEntity
                .ok(ApiResponse
                        .success("상담 예약 성공", response));
    }
}