package com.example.mesh_backend.apply.controller;

import com.example.mesh_backend.apply.dto.ApplyRequestDTO;
import com.example.mesh_backend.apply.service.ApplyService;
import com.example.mesh_backend.apply.service.ApplyServiceImpl;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.service.TokenService;
import com.example.mesh_backend.message.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Apply", description = "팀 지원하기 관련 API")
public class ApplyController {

    private final ApplyService applyService;
    private final TokenService tokenService;

    public ApplyController(ApplyServiceImpl applyService, TokenService tokenService) {
        this.applyService = applyService;
        this.tokenService = tokenService;
    }

    @PostMapping("/{project_id}/apply")
    @Operation(
            summary = "팀 지원하기 API",
            description = "로그인된 사용자가 특정 프로젝트에 지원합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "지원 성공"),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    public ResponseEntity<String> applyToPost(
            @Parameter(description = "지원하려는 프로젝트의 ID", required = true)
            @PathVariable("project_id") Long projectId,
            @Parameter(description = "사용자 인증 토큰", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "지원에 필요한 데이터")
            @RequestBody ApplyRequestDTO applyRequestDTO
    ) {
        // "Bearer " 접두사 제거
        String accessToken = token.replace("Bearer ", "");

        // 토큰에서 사용자 정보 추출
        User user = tokenService.getUserFromAccessToken(accessToken);

        // 서비스 호출
        String message = applyService.applyToProject(user, projectId, applyRequestDTO);
        return ResponseEntity.ok(message);
    }
}
