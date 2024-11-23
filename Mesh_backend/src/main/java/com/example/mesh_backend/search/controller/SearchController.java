package com.example.mesh_backend.search.controller;

import com.example.mesh_backend.mypage.dto.ProjectResponseDTO;
import com.example.mesh_backend.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/api/v1/search/result")
    public ResponseEntity<List<ProjectResponseDTO>> searchProjects(@RequestParam String keyword) {
        List<ProjectResponseDTO> projects = searchService.searchProjects(keyword);
        return ResponseEntity.ok(projects);
    }
}
