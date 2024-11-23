package com.example.mesh_backend.post.controller;

import com.example.mesh_backend.post.dto.PostResponseDTO;
import com.example.mesh_backend.post.service.PostService;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Post Search", description = "공고 검색 API")
@RequiredArgsConstructor
public class PostSearchController {

    private final PostService postService;
    private final TokenService tokenService;

    @GetMapping("/project")
    @Operation(summary = "조회수 기반 상위 공고 조회", description = "조회수 기준으로 상위 5개의 공고를 반환합니다.")
    public ResponseEntity<List<PostResponseDTO>> getTop5Projects(@RequestHeader("Authorization") String token) {
        // JWT에서 사용자 ID 추출
        String accessToken = token.replace("Bearer ", ""); // Bearer 접두사 제거
        User user = tokenService.getUserFromAccessToken(accessToken);
        Long userId = user.getUserId();

        // 조회수 기반 Top 5 프로젝트 가져오기
        List<PostResponseDTO> top5Projects = postService.getTop5ProjectsByViews(userId);

        return ResponseEntity.ok(top5Projects);
    }
}
