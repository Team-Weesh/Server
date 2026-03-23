package com.example.weesh.core.unavailableDate.application.useCase;

import com.example.weesh.core.unavailableDate.domain.UnavailableDate;

public interface UnavailableDateCreateUseCase {
    UnavailableDate createUnavailableDate(String date, String reason);
}
