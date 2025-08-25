package com.example.weesh.data.jpa.advice.mapper;

import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.foundation.enums.AdviceStatus;
import com.example.weesh.core.user.application.UserRepository;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.data.jpa.advice.AdviceEntity;
import com.example.weesh.data.jpa.user.UserEntity;
import com.example.weesh.data.jpa.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdviceMapper {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Advice toDomain(AdviceEntity entity) {
        if (entity == null) return null;
        return Advice.builder()
                .id(entity.getId())
                .desiredDate(entity.getDesiredDate())
                .desiredTime(entity.getDesiredTime())
                .content(entity.getContent())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .studentNumber(entity.getStudentNumber())
                .fullName(entity.getFullName())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .build();
    }

    public AdviceEntity toEntity(Advice advice) {
        if (advice == null) return null;
        AdviceEntity entity = new AdviceEntity();
        entity.setId(advice.getId());
        entity.setDesiredDate(advice.getDesiredDate());
        entity.setDesiredTime(advice.getDesiredTime());
        entity.setContent(advice.getContent());
        if (advice.getUserId() != null) {
            User user = userRepository.findById(advice.getUserId());
            UserEntity userEntity = userMapper.toEntity(user);
            entity.setUser(userEntity);
        }
        entity.setStudentNumber(advice.getStudentNumber());
        entity.setFullName(advice.getFullName());
        entity.setStatus(advice.getStatus() != null ? advice.getStatus() : AdviceStatus.PENDING);
        entity.setCreatedDate(advice.getCreatedDate() != null ? advice.getCreatedDate() : LocalDateTime.now());
        entity.setLastModifiedDate(advice.getLastModifiedDate() != null ? advice.getLastModifiedDate() : LocalDateTime.now());
        return entity;
    }
}