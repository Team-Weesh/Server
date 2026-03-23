package com.example.weesh.web.advice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdviceCreateRequestDto {
    @NotNull(message = "희망 날짜는 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "올바른 날짜 형식이 아닙니다. 예시: '20XX-XX-XX'")
    private String desiredDate;

    @NotNull(message = "희망 시간은 필수입니다.")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "올바른 시간 형식이 아닙니다. 예시: '09:00'")
    private String desiredTime;

    @NotBlank(message = "상담 내용은 필수입니다.")
    private String content;
}

