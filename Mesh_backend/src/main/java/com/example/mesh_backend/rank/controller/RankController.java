package com.example.mesh_backend.rank.controller;

import com.example.mesh_backend.rank.dto.UserProfileResponseDTO;
import com.example.mesh_backend.rank.dto.UserRankResponseDTO;
import com.example.mesh_backend.rank.service.RankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rank")
@Tag(name = "Rank", description = "랭킹 관련 API")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    // 상위 100명 랭킹 조회
    @GetMapping
    @Operation(summary = "랭킹 리스트 조회", description = "Mesh Score 기준으로 상위 100명의 랭킹을 반환합니다.")
    public ResponseEntity<List<UserRankResponseDTO>> getTopRankList() {
        List<UserRankResponseDTO> rankList = rankService.getTop100UsersByMeshScore();
        return ResponseEntity.ok(rankList);
    }

    // 나의 랭킹 조회
    @GetMapping("/me")
    @Operation(summary = "나의 랭킹 조회", description = "현재 사용자의 Mesh Score 순위를 반환합니다.")
    public ResponseEntity<UserRankResponseDTO> getMyRank(@RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token); // 토큰에서 userId 추출
        UserRankResponseDTO userRank = rankService.getUserRank(userId);
        return ResponseEntity.ok(userRank);
    }

    @GetMapping("/{user_id}")
    @Operation(summary = "특정 사용자 프로필 조회", description = "특정 사용자의 프로필을 조회합니다.")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@PathVariable("user_id") Long userId) {
        UserProfileResponseDTO userProfile = rankService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    // 토큰에서 userId 추출 메서드 (TokenService 활용)
    private Long extractUserIdFromToken(String token) {
        String accessToken = token.replace("Bearer ", "");
        // TokenService의 메서드를 사용하여 userId 추출
        // return tokenService.getUserFromAccessToken(accessToken).getUserId();
        return 1L; // 예제용으로 1L 반환
    }
}
