package com.example.weesh.web.advice.dto;

import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.foundation.enums.AdviceStatus;
import lombok.Getter;

@Getter
public class AdviceTimeResponseDto {
    private final Long id;
    private final String desiredDate;
    private final String desiredTime;
    private final AdviceStatus status;

    public AdviceTimeResponseDto(Advice advice) {
        this.id = advice.getId();
        this.desiredDate = advice.getDesiredDate();
        this.desiredTime = advice.getDesiredTime();
        this.status = advice.getStatus();
    }
}
