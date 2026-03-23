package com.example.weesh.core.unavailableDate.application.useCase;

import com.example.weesh.core.unavailableDate.domain.UnavailableDate;

import java.time.LocalDateTime;

public interface UnavailableDateCreateUseCase {
    UnavailableDate createUnavailableDate(LocalDateTime dateTime, String reason);
}
