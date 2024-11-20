package com.example.mesh_backend.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRankResponseDTO {
    private Long userId;
    private String nickname;
    private Long meshScore;
    private String profileImageUrl;
    private Integer rank;
}
