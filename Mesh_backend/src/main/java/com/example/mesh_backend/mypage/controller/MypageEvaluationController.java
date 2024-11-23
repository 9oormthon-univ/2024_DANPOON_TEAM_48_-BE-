package com.example.mesh_backend.mypage.controller;

import com.example.mesh_backend.login.security.CustomUserDetails;
import com.example.mesh_backend.mypage.dto.request.PeerEvaluationRequest;
import com.example.mesh_backend.mypage.dto.response.PeerEvaluationResponse;
import com.example.mesh_backend.mypage.service.MypageEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/projects/evaluations")
public class MypageEvaluationController {

    private final MypageEvaluationService evaluationService;

    // 1. 프로젝트 참여자 조회
    @GetMapping("/{projectId}")
    public ResponseEntity<List<PeerEvaluationResponse>> getProjectParticipants(
            @PathVariable Long projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<PeerEvaluationResponse> participants =
                evaluationService.getProjectParticipants(projectId, currentUser.getUser().getUserId());
        return ResponseEntity.ok(participants);
    }

    // 2. 동료 평가 제출
    @PostMapping("/{projectId}")
    public ResponseEntity<String> submitPeerEvaluations(
            @PathVariable Long projectId,
            @RequestBody List<PeerEvaluationRequest> evaluationRequests,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        evaluationService.submitPeerEvaluations(projectId, evaluationRequests, currentUser.getUser().getUserId());
        return ResponseEntity.ok("동료 평가가 반영되었습니다.");
    }
}
