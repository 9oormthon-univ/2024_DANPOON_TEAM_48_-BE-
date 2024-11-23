package com.example.mesh_backend.mypage.service;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import com.example.mesh_backend.mypage.entity.Award;
import com.example.mesh_backend.mypage.entity.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeshScoreService {

    private final UserRepository userRepository;

    @Transactional
    public void calculateAndSaveMeshScore(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        int totalScore = 0;

        //1. 수상 경력 점수 계산
        for (Award award : user.getAwards()) {
            switch (award.getScale()) {
                case 전국:
                    totalScore += 15;
                    break;
                case 시_군_구:
                    totalScore += 10;
                    break;
                case 대학:
                    totalScore += 7;
                    break;
                case 학부:
                    totalScore += 5;
                    break;
                default:
                    break;
            }
        }

        //2. 기술 스택 점수 계산
        for (Tool tool : user.getTools()) {
            if (tool.getProficiency() != null) {
                totalScore += tool.getProficiency();
            }
        }

        user.setMeshScore((long) totalScore);
        userRepository.save(user);
    }

}
