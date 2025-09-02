// User 도메인 모델, 최소한의 JPA 사용
package com.example.weesh.core.user.domain;

import com.example.weesh.core.foundation.enums.UserRole;
import com.example.weesh.data.jpa.advice.AdviceEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class User {
    private final Long id;
    private final String username;
    private final String password;
    private final String fullName;
    private final int studentNumber;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;
    private final Set<UserRole> roles;
    private final List<AdviceEntity> advices;

    @Builder
    public User(Long id, String username, String password, String fullName, int studentNumber,
                LocalDateTime createdDate, LocalDateTime lastModifiedDate, Set<UserRole> roles,
                List<AdviceEntity> advices) {
        // 기본 검증
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
        Objects.requireNonNull(fullName, "Full name cannot be null");

        this.id = id;
        this.username = username.trim();
        this.password = password;
        this.fullName = fullName.trim();
        this.studentNumber = studentNumber;

        LocalDateTime now = LocalDateTime.now();
        this.createdDate = createdDate != null ? createdDate : now;
        this.lastModifiedDate = lastModifiedDate != null ? lastModifiedDate : now;

        this.advices = advices;
        // 불변 컬렉션으로 보호
        this.roles = roles != null ?
                Collections.unmodifiableSet(new HashSet<>(roles)) :
                Collections.emptySet();
    }

    // 도메인 로직
    public boolean hasRole(UserRole role) {
        return roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }
}