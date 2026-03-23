package com.example.weesh.core.unavailableDate.application;

import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateCreateUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateDeleteUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateReadUseCase;
import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import com.example.weesh.web.unavailableDate.dto.UnavailableDateCreateRequestDto;
import com.example.weesh.web.unavailableDate.dto.UnavailableDateResponseDto;
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
    public UnavailableDateResponseDto createUnavailableDate(UnavailableDateCreateRequestDto dto) {
        if (unavailableDateRepository.existsByDate(dto.getDate())) {
            throw new IllegalArgumentException("이미 등록된 상담 불가 날짜입니다: " + dto.getDate());
        }

        UnavailableDate unavailableDate = UnavailableDate.builder()
                .date(dto.getDate())
                .reason(dto.getReason())
                .build();

        try {
            UnavailableDate saved = unavailableDateRepository.save(unavailableDate);
            return new UnavailableDateResponseDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 등록된 상담 불가 날짜입니다: " + dto.getDate());
        }
    }

    @Override
    public List<UnavailableDateResponseDto> getUnavailableDates() {
        return unavailableDateRepository.findAll().stream()
                .map(UnavailableDateResponseDto::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUnavailableDate(Long id) {
        unavailableDateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID: " + id + "의 상담 불가 날짜를 찾을 수 없습니다."));
        unavailableDateRepository.deleteById(id);
    }
}
