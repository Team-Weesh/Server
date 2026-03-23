package com.example.weesh.core.foundation.enums;

import lombok.Getter;

@Getter
public enum UnavailableTimeReason {
    UNAVAILABLE("상담 불가"),
    BOOKED("예약됨");

    private final String description;

    UnavailableTimeReason(String description) {
        this.description = description;
    }
}
