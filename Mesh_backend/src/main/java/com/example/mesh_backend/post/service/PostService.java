package com.example.mesh_backend.post.service;

import com.example.mesh_backend.post.dto.PostRequestDTO;

public interface PostService {
    String createPost(PostRequestDTO requestDTO, Long userId);
}
