package com.example.weesh.data.jpa.unavailableDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface JpaUnavailableDateRepository extends JpaRepository<UnavailableDateEntity, Long> {
    boolean existsByDate(String date);
}
