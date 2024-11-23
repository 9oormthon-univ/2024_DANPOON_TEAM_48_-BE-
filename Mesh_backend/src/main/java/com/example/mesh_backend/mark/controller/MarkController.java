package com.example.mesh_backend.mark.controller;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.service.TokenService;
import com.example.mesh_backend.mark.service.MarkService;
import com.example.mesh_backend.mypage.dto.response.ProjectResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marks")
@Tag(name = "Bookmark", description = "북마크 관련 API")
public class MarkController {

    private final MarkService markService;
    private final TokenService tokenService;

    public MarkController(MarkService markService, TokenService tokenService) {
        this.markService = markService;
        this.tokenService = tokenService;
    }
    @GetMapping
    @Operation(summary = "북마크된 프로젝트 조회 API", description = "사용자의 북마크된 프로젝트 목록을 반환합니다.")
    public ResponseEntity<List<ProjectResponseDTO>> getBookmarkedProjects(
            @RequestHeader("Authorization") String token
    ) {
        // 토큰에서 사용자 정보 추출
        User user = tokenService.getUserFromAccessToken(token.replace("Bearer ", ""));
        List<ProjectResponseDTO> bookmarkedProjects = markService.getBookmarkedProjects(user);
        return ResponseEntity.ok(bookmarkedProjects);
    }

    @PostMapping("/{project_id}")
    @Operation(summary = "북마크 토글 API", description = "특정 프로젝트에 대한 북마크 등록 또는 삭제")
    public ResponseEntity<String> toggleMark(
            @RequestHeader("Authorization") String token,
            @PathVariable("project_id") Long projectId
    ) {
        // 토큰에서 사용자 정보 추출
        User user = tokenService.getUserFromAccessToken(token.replace("Bearer ", ""));
        String message = markService.toggleMark(user, projectId);
        return ResponseEntity.ok(message);
    }
}
