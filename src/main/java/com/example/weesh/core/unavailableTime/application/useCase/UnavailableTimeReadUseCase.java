package com.example.weesh.core.unavailableTime.application.useCase;

import com.example.weesh.web.unavailableTime.dto.UnavailableTimeResponseDto;

import java.time.YearMonth;
import java.util.List;

public interface UnavailableTimeReadUseCase {
    List<UnavailableTimeResponseDto> getUnavailableTimes(YearMonth yearMonth);
}
