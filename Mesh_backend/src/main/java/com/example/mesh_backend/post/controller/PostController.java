package com.example.mesh_backend.post.controller;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.post.dto.PostRequestDTO;
import com.example.mesh_backend.post.service.PostService;
import com.example.mesh_backend.login.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "팀원 모집 공고", description = "공고 관련 API")
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final TokenService tokenService;

    public PostController(PostService postService, TokenService tokenService) {
        this.postService = postService;
        this.tokenService = tokenService;
    }

    @PostMapping
    @Operation(
            summary = "공고 등록 API",
            description = "사용자가 팀원을 모집하는 공고를 등록함."
    )
    public ResponseEntity<String> createPost(@RequestBody PostRequestDTO requestDTO,
                                             @RequestHeader("Authorization") String token) {
        // "Bearer " 접두사 제거
        String accessToken = token.replace("Bearer ", "");

        // AccessToken에서 User 정보 조회
        User user = tokenService.getUserFromAccessToken(accessToken);

        // userId 추출
        Long userId = user.getUserId();

        // PostService 호출
        String message = postService.createPost(requestDTO, userId);
        return ResponseEntity.ok(message);
    }
}
