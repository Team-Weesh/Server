package com.example.weesh.data.jpa.unavailableDate.mapper;

import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import com.example.weesh.data.jpa.unavailableDate.UnavailableDateEntity;
import org.springframework.stereotype.Component;

@Component
public class UnavailableDateMapper {

    public UnavailableDate toDomain(UnavailableDateEntity entity) {
        if (entity == null) return null;
        return UnavailableDate.builder()
                .id(entity.getId())
                .dateTime(entity.getDateTime())
                .reason(entity.getReason())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .build();
    }

    public UnavailableDateEntity toEntity(UnavailableDate domain) {
        if (domain == null) return null;
        UnavailableDateEntity entity = new UnavailableDateEntity();
        entity.setId(domain.getId());
        entity.setDateTime(domain.getDateTime());
        entity.setReason(domain.getReason());
        return entity;
    }
}
