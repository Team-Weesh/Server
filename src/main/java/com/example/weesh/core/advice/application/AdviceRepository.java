package com.example.weesh.core.advice.application;

import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface AdviceRepository {
    Advice save(Advice advice);
    Advice save(Advice advice, User user);
    Optional<Advice> findById(Long id);
    List<Advice> findAll();
    boolean existsByDateAndTime(String desiredDate, String desiredTime);
}