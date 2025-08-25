package com.example.weesh.core.advice.domain;

import com.example.weesh.core.foundation.enums.AdviceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Advice {
    private final Long id;
    private final LocalDateTime desiredDate;
    private final LocalDateTime desiredTime;
    private final String content;
    private final Long userId; // null 가능 (비로그인)
    private final Integer studentNumber; // null 가능 (로그인 시)
    private final String fullName; // null 가능 (로그인 시)
    private final AdviceStatus status;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    @Builder
    public Advice(Long id, LocalDateTime desiredDate, LocalDateTime desiredTime, String content, Long userId, Integer studentNumber, String fullName, AdviceStatus status, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.desiredDate = desiredDate;
        this.desiredTime = desiredTime;
        this.content = content;
        this.userId = userId;
        this.studentNumber = studentNumber;
        this.fullName = fullName;
        this.status = status;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }
}