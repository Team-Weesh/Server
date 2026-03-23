package com.example.weesh.web.unavailableTime.dto;

import com.example.weesh.core.foundation.enums.UnavailableTimeReason;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

public record UnavailableTimeResponseDto(LocalDateTime dateTime, UnavailableTimeReason reason) {
}
