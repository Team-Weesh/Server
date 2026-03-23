package com.example.weesh.core.unavailableDate.application;

import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateCreateUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateDeleteUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateReadUseCase;
import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnavailableDateService implements UnavailableDateCreateUseCase, UnavailableDateReadUseCase, UnavailableDateDeleteUseCase {
    private final UnavailableDateRepository unavailableDateRepository;

    @Override
    @Transactional
    public UnavailableDate createUnavailableDate(String date, String reason) {
        if (unavailableDateRepository.existsByDate(date)) {
            throw new IllegalArgumentException("이미 등록된 상담 불가 날짜입니다: " + date);
        }

        UnavailableDate unavailableDate = UnavailableDate.builder()
                .date(date)
                .reason(reason)
                .build();

        try {
            return unavailableDateRepository.save(unavailableDate);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 등록된 상담 불가 날짜입니다: " + date);
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
