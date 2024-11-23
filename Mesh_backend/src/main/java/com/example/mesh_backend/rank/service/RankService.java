package com.example.mesh_backend.rank.service;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.rank.dto.*;
import com.example.mesh_backend.rank.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;

    // 상위 100명의 사용자 조회
    public List<UserRankResponseDTO> getTop100UsersByMeshScore() {
        List<User> topUsers = rankRepository.findTop100ByOrderByMeshScoreDesc();
        return topUsers.stream()
                .map(user -> new UserRankResponseDTO(
                        user.getUserId(),
                        user.getNickname(),
                        user.getMeshScore(),
                        user.getProfileImageUrl(),
                        null // 순위는 별도로 반환하지 않음
                ))
                .collect(Collectors.toList());
    }

    // 나의 랭킹 조회
    public UserRankResponseDTO getUserRank(Long userId) {
        // 1. 현재 사용자의 정보 조회
        User user = rankRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 현재 사용자의 Mesh Score로 순위 계산
        Long userScore = user.getMeshScore();
        int rank = rankRepository.countByMeshScoreGreaterThan(userScore) + 1;

        // 3. UserRankResponseDTO 반환 (순위 포함)
        return new UserRankResponseDTO(
                user.getUserId(),
                user.getNickname(),
                user.getMeshScore(),
                user.getProfileImageUrl(),
                rank
        );
    }

    // 특정 사용자 랭킹 세부 조회
    public UserProfileResponseDTO getUserProfile(Long userId) {
        User user = rankRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Subcategories
        List<String> subcategories = user.getMaincategories().stream()
                .flatMap(maincategory -> maincategory.getSubcategories().stream())
                .map(subcategory -> subcategory.getSubcategoryName().toString())
                .collect(Collectors.toList());

        // Awards
        List<AwardResponseDTO> awards = user.getAwards().stream()
                .map(award -> new AwardResponseDTO(award.getProjectName(), award.getCertificateUrls(), award.getScale()))
                .collect(Collectors.toList());

        // Careers
        List<CareerResponseDTO> careers = user.getCareers().stream()
                .map(career -> new CareerResponseDTO(
                        career.getCareerContent(),
                        career.getDuration()))
                .collect(Collectors.toList());

        // Tools
        List<ToolResponseDTO> tools = user.getTools().stream()
                .map(tool -> new ToolResponseDTO(tool.getToolName(), tool.getProficiency()))
                .collect(Collectors.toList());

        return new UserProfileResponseDTO(
                user.getUserId(),
                user.getNickname(),
                user.getMeshScore(),
                user.getMajor(),
                user.getProfileImageUrl(),
                user.getContent(),
                subcategories,
                awards,
                careers,
                tools
        );
    }
}
