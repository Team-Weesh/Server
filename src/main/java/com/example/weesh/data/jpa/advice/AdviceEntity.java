package com.example.weesh.data.jpa.advice;

import com.example.weesh.core.foundation.enums.AdviceStatus;
import com.example.weesh.data.jpa.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "Advice")
public class AdviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상담 희망 날짜
    @Column(name = "desired_date", nullable = false)
    private String desiredDate;

    // 상담 희망 시간
    @Column(name = "desired_time", nullable = false)
    private String desiredTime;

    // 상담 이유
    @Column(name = "content", nullable = false)
    private String content;

    // 상담 신청자 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;

    // 학번 (비로그인 시 입력)
    @Column(name = "student_number")
    private Integer studentNumber;

    // 이름 (비로그인 시 입력)
    @Column(name = "full_name")
    private String fullName;

    // 상담 신청 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AdviceStatus status = AdviceStatus.PENDING;

    // 상담 요청 보낸 시간
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // 상담 요청 수정 시간
    @Column(name = "last_modified_date", nullable = false)
    private LocalDateTime lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdDate == null) {
            createdDate = now;
        }
        if (lastModifiedDate == null) {
            lastModifiedDate = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = LocalDateTime.now();
    }
}
