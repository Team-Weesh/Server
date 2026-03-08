package com.example.weesh.data.jpa.advice;

import com.example.weesh.core.advice.application.AdviceRepository;
import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.foundation.enums.AdviceStatus;
import com.example.weesh.core.user.domain.User;
import com.example.weesh.data.jpa.advice.mapper.AdviceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdviceRepositoryImpl implements AdviceRepository {
    private final JpaAdviceRepositoryImpl jpaRepository;
    private final AdviceMapper adviceMapper;

    @Override
    public Advice save(Advice advice, User user) {
        AdviceEntity entity = adviceMapper.toEntity(advice, user);
        AdviceEntity savedEntity = jpaRepository.save(entity);
        return adviceMapper.toDomain(savedEntity);
    }

    @Override
    public Advice save(Advice advice) {
        return save(advice, null);
    }

    @Override
    public Optional<Advice> findById(Long id) {
        return jpaRepository.findById(id).map(adviceMapper::toDomain);
    }

    @Override
    public List<Advice> findAll() {
        return jpaRepository.findAll().stream()
                .map(adviceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDateAndTime(String desiredDate, String desiredTime) {
        return jpaRepository.existsByDesiredDateAndDesiredTimeAndStatusNot(desiredDate, desiredTime, AdviceStatus.DELETED);
    }
}