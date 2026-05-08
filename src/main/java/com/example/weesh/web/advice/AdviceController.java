package com.example.weesh.web.advice;

import com.example.weesh.core.advice.application.useCase.AdviceApproveUseCase;
import com.example.weesh.core.advice.application.useCase.AdviceCreateUseCase;
import com.example.weesh.core.advice.application.useCase.AdviceDeleteUseCase;
import com.example.weesh.core.advice.application.useCase.AdviceReadUseCase;
import com.example.weesh.core.advice.application.useCase.AdviceUpdateUseCase;
import com.example.weesh.core.foundation.log.LoggingUtil;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.web.advice.dto.AdviceCreateRequestDto;
import com.example.weesh.web.advice.dto.AdviceResponseDto;
import com.example.weesh.web.advice.dto.AdviceTimeResponseDto;
import com.example.weesh.web.advice.dto.AdviceUpdateRequestDro;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/advice")
@Tag(name = "Advice API", description = "상담 예약 API")
@RequiredArgsConstructor
public class AdviceController {
    private final AdviceCreateUseCase adviceCreateUseCase;
    private final AdviceReadUseCase adviceReadUseCase;
    private final AdviceApproveUseCase adviceApproveUseCase;
    private final AdviceUpdateUseCase adviceUpdateUseCase;
    private final AdviceDeleteUseCase adviceDeleteUseCase;

    @Operation(summary = "상담 예약", description = "로그인한 사용자가 상담을 예약합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "클라이언트 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "5XX", description = "서버 오류")
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
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

    @Operation(summary = "내 상담 예약 시간 조회", description = "로그인한 학생이 본인의 상담 예약 시간을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "클라이언트 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "5XX", description = "서버 오류")
    })
    @GetMapping("/my-times")
    public ResponseEntity<ApiResponse<List<AdviceTimeResponseDto>>> getMyAdviceTimes(HttpServletRequest request) {
        List<AdviceTimeResponseDto> response = adviceReadUseCase.getMyAdviceTimes(request);
        return ResponseEntity
                .ok(ApiResponse
                        .success("내 상담 예약 시간 조회 성공", response));
    }

    @Operation(summary = "상담 예약 승인", description = "관리자 권한으로 상담 예약을 승인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "클라이언트 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "5XX", description = "서버 오류")
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

    @Operation(summary = "상담 예약 수정", description = "관리자 권한으로 상담 예약을 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "클라이언트 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "5XX", description = "서버 오류")
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

    @Operation(summary = "상담 예약 삭제", description = "관리자 권한으로 상담 예약을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "4XX", description = "클라이언트 오류"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "5XX", description = "서버 오류")
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
