package com.example.mesh_backend.rank.service;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.rank.dto.UserRankResponseDTO;
import com.example.mesh_backend.rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;

    // 상위 100명의 사용자 랭킹 조회
    public List<UserRankResponseDTO> getTop100UsersByMeshScore() {
        List<User> topUsers = rankRepository.findTop100ByOrderByMeshScoreDesc();
        return topUsers.stream()
                .map(user -> new UserRankResponseDTO(
                        user.getUserId(),
                        user.getNickname(),
                        user.getMeshScore(),
                        user.getProfileImageUrl()
                ))
                .collect(Collectors.toList());
    }

    // 특정 사용자 랭킹 조회
    public UserRankResponseDTO getUserRank(Long userId) {
        User user = rankRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserRankResponseDTO(user.getUserId(), user.getNickname(), user.getMeshScore(), user.getProfileImageUrl());
    }
}
