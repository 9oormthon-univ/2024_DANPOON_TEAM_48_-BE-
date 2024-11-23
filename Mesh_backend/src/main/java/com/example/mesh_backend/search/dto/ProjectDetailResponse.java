package com.example.mesh_backend.search.dto;

import com.example.mesh_backend.post.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProjectDetailResponse {
    private Long postId;
    private String postTitle;
    private String postContents;
    private String projectImageUrl;
    private String projectFile;
    private Long views;
    private LocalDate deadline;
    private LocalDate createAt;
    private Status status;
    private String nickname;
    private String profileImageUrl;
    private boolean isMarked;
}
