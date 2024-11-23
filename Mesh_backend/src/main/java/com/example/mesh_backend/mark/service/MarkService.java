package com.example.mesh_backend.mark.service;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.mypage.dto.response.ProjectResponseDTO;

import java.util.List;

public interface MarkService {
    String toggleMark(User user, Long projectId);
    List<ProjectResponseDTO> getBookmarkedProjects(User user);
}
