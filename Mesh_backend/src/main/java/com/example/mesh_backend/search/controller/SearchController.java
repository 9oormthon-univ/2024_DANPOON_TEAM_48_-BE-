package com.example.mesh_backend.search.controller;

import com.example.mesh_backend.search.dto.ProjectDetailResponse;
import com.example.mesh_backend.search.dto.ProjectResponseDTO;
import com.example.mesh_backend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/result")
    public ResponseEntity<List<ProjectResponseDTO>> searchProjects(
            @RequestParam String keyword,
            @RequestParam(required = false) List<String> categories // 클라이언트에서 카테고리 필터 전달
    ) {
        List<ProjectResponseDTO> projects = searchService.searchProjects(keyword, categories);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/{project_id}")
    public ResponseEntity<ProjectDetailResponse> getProjectDetail(
            @PathVariable("project_id") Long projectId,
            @RequestHeader("Authorization") String token
    ) {
        ProjectDetailResponse projectDetail = searchService.getProjectDetail(projectId, token);
        return ResponseEntity.ok(projectDetail);
    }

}
