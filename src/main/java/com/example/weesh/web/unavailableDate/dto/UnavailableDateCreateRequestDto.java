package com.example.weesh.web.unavailableDate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class UnavailableDateCreateRequestDto {
    @NotNull(message = "날짜는 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "올바른 날짜 형식이 아닙니다. 예시: '20XX-XX-XX'")
    private String date;

    @NotNull(message = "시간은 필수입니다.")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "올바른 시간 형식이 아닙니다. 예시: '09:00'")
    private String time;

    private String reason;

    public LocalDateTime toDateTime() {
        return LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time));
    }
}
