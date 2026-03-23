package com.example.weesh.core.unavailableDate.application;

import com.example.weesh.core.unavailableDate.domain.UnavailableDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UnavailableDateRepository {
    UnavailableDate save(UnavailableDate unavailableDate);
    Optional<UnavailableDate> findById(Long id);
    List<UnavailableDate> findAll();
    boolean existsByDateTime(LocalDateTime dateTime);
    void deleteById(Long id);
}
