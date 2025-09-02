package com.example.weesh.web.advice.dto;

import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.foundation.enums.AdviceStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdviceResponseDto {
    private final Long id;
    private final String desiredDate;
    private final String desiredTime;
    private final String content;
    private final Long userId;
    private final Integer studentNumber;
    private final String fullName;
    private final AdviceStatus status;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    public AdviceResponseDto(Advice advice) {
        this.id = advice.getId();
        this.desiredDate = advice.getDesiredDate();
        this.desiredTime = advice.getDesiredTime();
        this.content = advice.getContent();
        this.userId = advice.getUserId();
        this.studentNumber = advice.getStudentNumber();
        this.fullName = advice.getFullName();
        this.status = advice.getStatus();
        this.createdDate = advice.getCreatedDate();
        this.lastModifiedDate = advice.getLastModifiedDate();
    }
}
