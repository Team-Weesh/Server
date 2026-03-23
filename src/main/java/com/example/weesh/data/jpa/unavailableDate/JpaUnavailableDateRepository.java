package com.example.weesh.data.jpa.unavailableDate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaUnavailableDateRepository extends JpaRepository<UnavailableDateEntity, Long> {
    boolean existsByDateTime(LocalDateTime dateTime);
    List<UnavailableDateEntity> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
