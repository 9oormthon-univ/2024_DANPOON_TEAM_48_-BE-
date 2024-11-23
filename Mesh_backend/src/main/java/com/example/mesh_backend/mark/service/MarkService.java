package com.example.mesh_backend.mark.service;

import com.example.mesh_backend.login.entity.User;

public interface MarkService {
    String toggleMark(User user, Long projectId);
}
