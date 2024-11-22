package com.example.mesh_backend.apply.service;

import com.example.mesh_backend.apply.dto.ApplyRequestDTO;
import com.example.mesh_backend.login.entity.User;

public interface ApplyService {
    String applyToProject(User user, Long projectId, ApplyRequestDTO applyRequestDTO);
}
