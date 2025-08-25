package com.example.weesh.core.advice.application;

import com.example.weesh.core.advice.domain.Advice;

import java.util.Optional;

public interface AdviceRepository {
    Advice save(Advice advice);
    Optional<Advice> findById(Long id);
}