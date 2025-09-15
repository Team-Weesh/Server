package com.example.weesh.data.jpa.advice;

import com.example.weesh.core.foundation.enums.AdviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAdviceRepositoryImpl extends JpaRepository<AdviceEntity, Long> {
    boolean existsByDesiredDateAndDesiredTimeAndStatusNot(String desiredDate, String desiredTime, AdviceStatus status);
}