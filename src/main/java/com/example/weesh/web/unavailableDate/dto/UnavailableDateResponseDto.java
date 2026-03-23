package com.example.weesh.web.unavailableDate.dto;

import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UnavailableDateResponseDto {
    private final Long id;
    private final LocalDateTime dateTime;
    private final String reason;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    public UnavailableDateResponseDto(UnavailableDate unavailableDate) {
        this.id = unavailableDate.getId();
        this.dateTime = unavailableDate.getDateTime();
        this.reason = unavailableDate.getReason();
        this.createdDate = unavailableDate.getCreatedDate();
        this.lastModifiedDate = unavailableDate.getLastModifiedDate();
    }
}
