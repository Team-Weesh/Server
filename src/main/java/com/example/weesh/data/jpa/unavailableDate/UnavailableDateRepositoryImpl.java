package com.example.weesh.data.jpa.unavailableDate;

import com.example.weesh.core.unavailableDate.application.UnavailableDateRepository;
import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import com.example.weesh.data.jpa.unavailableDate.mapper.UnavailableDateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnavailableDateRepositoryImpl implements UnavailableDateRepository {
    private final JpaUnavailableDateRepository jpaRepository;
    private final UnavailableDateMapper mapper;

    @Override
    public UnavailableDate save(UnavailableDate unavailableDate) {
        UnavailableDateEntity entity = mapper.toEntity(unavailableDate);
        UnavailableDateEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UnavailableDate> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<UnavailableDate> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDateTime(LocalDateTime dateTime) {
        return jpaRepository.existsByDateTime(dateTime);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
