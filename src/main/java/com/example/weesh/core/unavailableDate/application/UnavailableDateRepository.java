package com.example.weesh.core.unavailableDate.application;

import com.example.weesh.core.unavailableDate.domain.UnavailableDate;

import java.util.List;
import java.util.Optional;

public interface UnavailableDateRepository {
    UnavailableDate save(UnavailableDate unavailableDate);
    Optional<UnavailableDate> findById(Long id);
    List<UnavailableDate> findAll();
    boolean existsByDate(String date);
    void deleteById(Long id);
}
