package com.example.mesh_backend.mypage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PeerEvaluationRequest {
    private Long userId; // 평가 대상 유저 ID
    private int attendanceScore;
    private int contributionScore;
    private int communicationScore;
    private int profileScore;
}
