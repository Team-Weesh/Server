package com.example.weesh.core.unavailableTime.application;

import com.example.weesh.core.advice.application.AdviceRepository;
import com.example.weesh.core.advice.domain.Advice;
import com.example.weesh.core.foundation.enums.UnavailableTimeReason;
import com.example.weesh.core.unavailableDate.application.UnavailableDateRepository;
import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import com.example.weesh.core.unavailableTime.application.useCase.UnavailableTimeReadUseCase;
import com.example.weesh.web.unavailableTime.dto.UnavailableTimeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnavailableTimeService implements UnavailableTimeReadUseCase {
    private final UnavailableDateRepository unavailableDateRepository;
    private final AdviceRepository adviceRepository;

    @Override
    public List<UnavailableTimeResponseDto> getUnavailableTimes(YearMonth yearMonth) {
        List<UnavailableTimeResponseDto> result = new ArrayList<>();

        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
        List<UnavailableDate> unavailableDates = unavailableDateRepository.findByDateTimeBetween(start, end);
        for (UnavailableDate ud : unavailableDates) {
            result.add(new UnavailableTimeResponseDto(ud.getDateTime(), UnavailableTimeReason.UNAVAILABLE));
        }

        String yearMonthStr = yearMonth.toString();
        List<Advice> bookedAdvices = adviceRepository.findActiveByDesiredDateStartingWith(yearMonthStr);
        for (Advice advice : bookedAdvices) {
            LocalDateTime dateTime = LocalDateTime.of(
                    LocalDate.parse(advice.getDesiredDate()),
                    LocalTime.parse(advice.getDesiredTime())
            );
            result.add(new UnavailableTimeResponseDto(dateTime, UnavailableTimeReason.BOOKED));
        }

        return result;
    }
}
