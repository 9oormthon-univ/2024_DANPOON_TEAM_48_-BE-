package com.example.mesh_backend.mypage.service;

import com.example.mesh_backend.mypage.dto.response.ProjectResponseDTO;
import com.example.mesh_backend.mypage.dto.response.ProjectDetailResponse;

import java.util.List;

public interface MypageProjectService {
    List<ProjectResponseDTO> getUserProjects(Long userId);
    ProjectDetailResponse getProjectDetail(Long projectId, Long userId);
}
