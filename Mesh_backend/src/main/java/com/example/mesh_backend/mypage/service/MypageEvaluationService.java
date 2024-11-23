package com.example.mesh_backend.mypage.service;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import com.example.mesh_backend.mypage.dto.request.PeerEvaluationRequest;
import com.example.mesh_backend.mypage.dto.response.PeerEvaluationResponse;
import com.example.mesh_backend.post.entity.Project;
import com.example.mesh_backend.post.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageEvaluationService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    // 1. 프로젝트 참여자 조회
    @Transactional(readOnly = true)
    public List<PeerEvaluationResponse> getProjectParticipants(Long projectId, Long currentUserId) {
        List<Project> projects = projectRepository.findAllByPost_PostId(projectId);

        // 참여자 조회 후 현재 사용자 제외
        return projects.stream()
                .filter(project -> !project.getUser().getUserId().equals(currentUserId))
                .map(project -> {
                    User user = project.getUser();
                    return new PeerEvaluationResponse(
                            user.getUserId(),
                            user.getNickname(),
                            user.getProfileImageUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    // 2. 동료 평가 제출
    @Transactional
    public void submitPeerEvaluations(Long projectId, List<PeerEvaluationRequest> evaluationRequests, Long currentUserId) {
        for (PeerEvaluationRequest request : evaluationRequests) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 유저 ID입니다."));

            // 평균 점수 계산
            int averageScore = (request.getAttendanceScore() +
                    request.getContributionScore() +
                    request.getCommunicationScore() +
                    request.getProfileScore()) / 4;

            // 메시스코어 업데이트
            user.setMeshScore(user.getMeshScore() + averageScore);
            userRepository.save(user);
        }
    }
}
