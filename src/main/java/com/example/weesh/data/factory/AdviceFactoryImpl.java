package com.example.weesh.data.factory;

import com.example.weesh.core.advice.application.factory.AdviceFactory;
import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.foundation.enums.AdviceStatus;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AdviceFactoryImpl implements AdviceFactory {
    @Override
    public Advice createAdvice(AdviceCreateRequestDto dto, Long userId) {
        return Advice.builder()
                .desiredDate(dto.getDesiredDate())
                .desiredTime(dto.getDesiredTime())
                .content(dto.getContent())
                .userId(userId)
                .studentNumber(dto.getStudentNumber())
                .fullName(dto.getFullName())
                .status(AdviceStatus.PENDING)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

//    private void validateRequestDto(AdviceCreateRequestDto dto) {
//        if (dto.getContent() == null || dto.getUsername().trim().isEmpty()) {
//            throw new IllegalArgumentException("아이디는 null 또는 빈 값일 수 없습니다.");
//        }
//        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
//            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
//        }
//        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
//            throw new IllegalArgumentException("이름은 null 또는 빈 값일 수 없습니다.");
//        }
//    }
}
