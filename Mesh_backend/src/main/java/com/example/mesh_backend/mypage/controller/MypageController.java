package com.example.mesh_backend.mypage.controller;

import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.security.CustomUserDetails;
import com.example.mesh_backend.message.BasicResponse;
import com.example.mesh_backend.mypage.dto.request.UserProfileRequest;
import com.example.mesh_backend.mypage.service.MeshScoreService;
import com.example.mesh_backend.mypage.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "마이페이지", description = "마이페이지 API")
public class MypageController {


    private final MypageService mypageService;
    private final MeshScoreService meshScoreService;

    //1. 내 정보 수정
    @Operation(summary = "프로필 수정", description = "내 정보 수정(profile_edit) API")
    @PatchMapping("/profile")
    public ResponseEntity<BasicResponse<String>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserProfileRequest request) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        User user = customUserDetails.getUser();

        try {
            mypageService.updateUserProfile(user, request);
            meshScoreService.calculateAndSaveMeshScore(user.getUserId());
            return ResponseEntity.ok(BasicResponse.ofSuccess("프로필 수정 완료"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.DUPLICATE_NICKNAME));
        }
    }
}