package com.example.mesh_backend.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponseDTO {
    private Long postId;
    private String postTitle;
    private String status;
    private int views;
    private String projectImageUrl;
    private String nickname;
    private String profileImageUrl;
    private boolean bookmarked;
    private List<String> categories;
}

