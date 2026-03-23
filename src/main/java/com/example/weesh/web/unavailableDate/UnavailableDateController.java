package com.example.weesh.web.unavailableDate;

import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateCreateUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateDeleteUseCase;
import com.example.weesh.core.unavailableDate.application.useCase.UnavailableDateReadUseCase;
import com.example.weesh.core.unavailableDate.domain.UnavailableDate;
import com.example.weesh.web.unavailableDate.dto.UnavailableDateCreateRequestDto;
import com.example.weesh.web.unavailableDate.dto.UnavailableDateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unavailable-dates")
@Tag(name = "UnavailableDate API", description = "상담 불가 날짜 관련 API")
@RequiredArgsConstructor
public class UnavailableDateController {
    private final UnavailableDateCreateUseCase createUseCase;
    private final UnavailableDateReadUseCase readUseCase;
    private final UnavailableDateDeleteUseCase deleteUseCase;

    @Operation(summary = "상담 불가 날짜 등록", description = "관리자가 상담 불가 날짜를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4xx || 5xx", description = "실패")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<UnavailableDateResponseDto>> createUnavailableDate(
            @Valid @RequestBody UnavailableDateCreateRequestDto dto) {
        UnavailableDate created = createUseCase.createUnavailableDate(dto.getDate(), dto.getReason());
        UnavailableDateResponseDto response = new UnavailableDateResponseDto(created);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("상담 불가 날짜 등록 성공", response, 201));
    }

    @Operation(summary = "상담 불가 날짜 전체 조회", description = "등록된 상담 불가 날짜 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<UnavailableDateResponseDto>>> getUnavailableDates() {
        List<UnavailableDateResponseDto> response = readUseCase.getUnavailableDates().stream()
                .map(UnavailableDateResponseDto::new)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("상담 불가 날짜 조회 성공", response));
    }

    @Operation(summary = "상담 불가 날짜 삭제", description = "관리자가 상담 불가 날짜를 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4xx || 5xx", description = "실패")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUnavailableDate(@PathVariable Long id) {
        deleteUseCase.deleteUnavailableDate(id);
        return ResponseEntity.ok(ApiResponse.success("상담 불가 날짜 삭제 성공", null));
    }
}
