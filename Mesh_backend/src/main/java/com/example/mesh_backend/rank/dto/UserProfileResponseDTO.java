package com.example.mesh_backend.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long userId;
    private String nickname;
    private Long meshScore;
    private String major;
    private String profileImageUrl;
    private String content;
    private List<String> subcategories; // 키워드
    private List<AwardResponseDTO> awards;
    private List<CareerResponseDTO> careers;
    private List<ToolResponseDTO> tools;
}
