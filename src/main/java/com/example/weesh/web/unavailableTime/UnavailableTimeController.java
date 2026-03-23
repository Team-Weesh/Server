package com.example.weesh.web.unavailableTime;

import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.core.unavailableTime.application.useCase.UnavailableTimeReadUseCase;
import com.example.weesh.web.unavailableTime.dto.UnavailableTimeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/unavailable-times")
@Tag(name = "UnavailableTime API", description = "예약 불가 시간 통합 조회 API")
@RequiredArgsConstructor
@Validated
public class UnavailableTimeController {
    private final UnavailableTimeReadUseCase readUseCase;

    @Operation(summary = "예약 불가 시간 통합 조회", description = "상담 불가 시간과 이미 예약된 시간을 월 단위로 통합 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "클라이언트 오류")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UnavailableTimeResponseDto>>> getUnavailableTimes(
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "올바른 연월 형식이 아닙니다. 예시: '2026-03'") String yearMonth) {
        List<UnavailableTimeResponseDto> response = readUseCase.getUnavailableTimes(YearMonth.parse(yearMonth));
        return ResponseEntity.ok(ApiResponse.success("예약 불가 시간 조회 성공", response));
    }
}
