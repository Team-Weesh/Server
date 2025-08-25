package com.example.weesh.data.jpa.advice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAdviceRepositoryImpl extends JpaRepository<AdviceEntity, Long> {
}