package com.example.weesh.core.unavailableDate.application;

import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateCreateUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateDeleteUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateReadUseCase;
import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnavailableDateService implements UnavailableDateCreateUseCase, UnavailableDateReadUseCase, UnavailableDateDeleteUseCase {
    private final UnavailableDateRepository unavailableDateRepository;

    @Override
    @Transactional
    public UnavailableDate createUnavailableDate(LocalDateTime dateTime, String reason) {
        if (unavailableDateRepository.existsByDateTime(dateTime)) {
            throw new IllegalArgumentException("이미 등록된 상담 불가 시간입니다: " + dateTime);
        }

        UnavailableDate unavailableDate = UnavailableDate.builder()
                .dateTime(dateTime)
                .reason(reason)
                .build();

        try {
            return unavailableDateRepository.save(unavailableDate);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("상담 불가 시간 저장 중 데이터 무결성 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<UnavailableDate> getUnavailableDates() {
        return unavailableDateRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUnavailableDate(Long id) {
        unavailableDateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + id + "의 상담 불가 날짜를 찾을 수 없습니다."));
        unavailableDateRepository.deleteById(id);
    }
}
