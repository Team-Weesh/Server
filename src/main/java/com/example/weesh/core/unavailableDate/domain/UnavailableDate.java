package com.example.weesh.core.unavailableDate.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UnavailableDate {
    private final Long id;
    private String date;
    private String reason;
    private final LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    @Builder
    public UnavailableDate(Long id, String date, String reason, LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.date = date;
        this.reason = reason;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public void update(String date, String reason) {
        boolean changed = false;

        if (date != null && !date.equals(this.date)) {
            this.date = date;
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
