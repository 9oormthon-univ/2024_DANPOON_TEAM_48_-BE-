package com.example.mesh_backend.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PeerEvaluationResponse {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
}
