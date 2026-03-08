package com.example.weesh.web.advice;

import com.example.weesh.core.advice.application.AdviceService;
import com.example.weesh.core.advice.application.useCase.*;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;
import com.example.weesh.web.advice.dto.AdviceResponseDto;
import com.example.weesh.web.advice.dto.AdviceUpdateRequestDro;
import com.example.weesh.web.auth.dto.AuthRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/advice")
@Tag(name = "Advice API", description = "상담 관련 API")
@RequiredArgsConstructor
public class AdviceController {
    private final AdviceCreateUseCase adviceCreateUseCase;
    private final AdviceReadUseCase adviceReadUseCase;
    private final AdviceApproveUseCase adviceApproveUseCase;
    private final AdviceUpdateUseCase adviceUpdateUseCase;
    private final AdviceDeleteUseCase adviceDeleteUseCase;

    @Operation(summary = "상담 예약", description = "회원/비회원 상담 예약")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx || 5xx",
                    description = "실패"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "상담 예약 요청 DTO",
            required = true,
            content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = AdviceCreateRequestDto.class)
            )
    )
    @PostMapping
    public ResponseEntity<ApiResponse<AdviceResponseDto>> createAdvice(
            @Valid @RequestBody AdviceCreateRequestDto dto,
            HttpServletRequest request) {
        AdviceResponseDto response = adviceCreateUseCase.createAdvice(dto, request);

        return ResponseEntity
                .ok(ApiResponse
                        .success("상담 예약 성공", response));
    }


    @Operation(summary = "상담 예약 전체 조회", description = "관리자 권한")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<ResponseEntity<ApiResponse<AdviceResponseDto>>> getAdvice() {
        List<AdviceResponseDto> adviceList = adviceReadUseCase.getAdvice();
        return adviceList.stream()
                .map(advice -> ResponseEntity
                        .ok(ApiResponse
                                .success("상담 내역 조회 성공", advice)))
                .toList();
    }

    @Operation(summary = "상담 예약 승인", description = "관리자 권한으로 상담 예약 승인")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx || 5xx",
                    description = "실패"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<AdviceResponseDto>> approveAdvice(@PathVariable Long id) {
        AdviceResponseDto response = adviceApproveUseCase.approveAdvice(id);

        LoggingUtil.info("Advice approved with ID: {}", String.valueOf(id));
        return ResponseEntity
                .ok(ApiResponse
                        .success("상담 예약 승인 성공", response));
    }

    @Operation(summary = "상담 예약 수정", description = "사용자가 자신의 상담 예약 수정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx || 5xx",
                    description = "실패"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/update")
    public ResponseEntity<ApiResponse<AdviceResponseDto>> updateAdvice(
            @PathVariable Long id,
            @Valid @RequestBody AdviceUpdateRequestDro dto) {
        AdviceResponseDto response = adviceUpdateUseCase.updateAdvice(id, dto);

        LoggingUtil.info("Advice updated with ID: {}", String.valueOf(id));
        return ResponseEntity
                .ok(ApiResponse
                        .success("상담 예약 수정 성공", response));
    }

    @Operation(summary = "상담 예약 삭제", description = "사용자가 자신의 상담 예약 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "4xx || 5xx",
                    description = "실패"
            )
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<ApiResponse<AdviceResponseDto>> deleteAdvice(@PathVariable Long id) {
        AdviceResponseDto response = adviceDeleteUseCase.deleteAdvice(id);

        LoggingUtil.info("Advice deleted with ID: {}", String.valueOf(id));
        return ResponseEntity
                .ok(ApiResponse
                        .success("상담 예약 삭제 성공", response));
    }
}