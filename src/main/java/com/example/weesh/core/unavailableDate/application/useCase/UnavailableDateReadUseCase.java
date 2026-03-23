package com.example.weesh.core.unavailableDate.application.useCase;

import com.example.weesh.core.unavailableDate.domain.UnavailableDate;

import java.util.List;

public interface UnavailableDateReadUseCase {
    List<UnavailableDate> getUnavailableDates();
}
