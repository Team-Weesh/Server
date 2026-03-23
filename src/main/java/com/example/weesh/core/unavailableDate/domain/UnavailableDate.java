package com.example.weesh.core.unavailableDate.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UnavailableDate {
    private final Long id;
    private LocalDateTime dateTime;
    private String reason;
    private final LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Builder
    public UnavailableDate(Long id, LocalDateTime dateTime, String reason, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.dateTime = dateTime;
        this.reason = reason;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public void update(LocalDateTime dateTime, String reason) {
        boolean changed = false;

        if (dateTime != null && !dateTime.equals(this.dateTime)) {
            this.dateTime = dateTime;
            changed = true;
        }
        if (reason != null && !reason.equals(this.reason)) {
            this.reason = reason;
            changed = true;
        }

        if (changed) {
            this.lastModifiedDate = LocalDateTime.now();
        }
    }
}
