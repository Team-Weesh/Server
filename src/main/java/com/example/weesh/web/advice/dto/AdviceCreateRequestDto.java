package com.example.weesh.web.advice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdviceCreateRequestDto {
    @NotNull(message = "희망 날짜는 필수입니다.")
    private LocalDateTime desiredDate;

    @NotNull(message = "희망 시간은 필수입니다.")
    private LocalDateTime desiredTime;

    @NotBlank(message = "상담 내용은 필수입니다.")
    private String content;

    private Integer studentNumber; // 비로그인 시 필수

    private String fullName; // 비로그인 시 필수
}

