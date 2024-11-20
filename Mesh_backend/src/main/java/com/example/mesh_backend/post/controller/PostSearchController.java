package com.example.mesh_backend.post.controller;

import com.example.mesh_backend.post.dto.PostResponseDTO;
import com.example.mesh_backend.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@Tag(name = "Post Search", description = "공고 검색 API")
@RequiredArgsConstructor
public class PostSearchController {

    private final PostService postService;

    @GetMapping("/project")
    @Operation(summary = "조회수 기반 상위 공고 조회", description = "조회수 기준으로 상위 5개의 공고를 반환합니다.")
    public ResponseEntity<List<PostResponseDTO>> getTop5Projects() {
        List<PostResponseDTO> top5Projects = postService.getTop5Projects();
        return ResponseEntity.ok(top5Projects);
    }
}
