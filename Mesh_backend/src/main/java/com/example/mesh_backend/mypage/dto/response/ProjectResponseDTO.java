package com.example.mesh_backend.mypage.dto.response;

import com.example.mesh_backend.post.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long postId;               // 프로젝트 ID
    private String postTitle;          // 프로젝트 제목
    private Status status;             // 프로젝트 상태 (모집중, 활동중 등)
    private Long views;                // 조회수
    private String projectImageUrl;    // 프로젝트 이미지 URL
    private String nickname;           // 작성자 닉네임
    private String profileImageUrl;    // 작성자 프로필 이미지 URL
    private boolean isBookmarked;      // 북마크 여부
    private int dDay; //D-Day 정보 추가
}
