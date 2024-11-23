package com.example.mesh_backend.mypage.dto.response;

import com.example.mesh_backend.post.entity.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
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
