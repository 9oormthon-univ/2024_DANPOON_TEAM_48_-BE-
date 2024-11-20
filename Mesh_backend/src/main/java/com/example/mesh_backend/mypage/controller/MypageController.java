package com.example.mesh_backend.mypage.controller;

import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.security.CustomUserDetails;
import com.example.mesh_backend.login.service.UserService;
import com.example.mesh_backend.message.BasicResponse;
import com.example.mesh_backend.mypage.dto.request.UserProfileRequest;
import com.example.mesh_backend.mypage.service.MeshScoreService;
import com.example.mesh_backend.mypage.service.MypageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "마이페이지", description = "마이페이지 API")
public class MypageController {


    private final MypageService mypageService;
    private final MeshScoreService meshScoreService;
    private final UserService userService;

    //1. 내 정보 수정
    @PatchMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 수정 및 이미지 업데이트", description = "내 정보와 프로필 이미지를 수정하는 API")
    public ResponseEntity<BasicResponse<String>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value = "userUpdateRequest", required = false) String userUpdateRequestJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        User user = customUserDetails.getUser();

        try {
            if (userUpdateRequestJson != null && !userUpdateRequestJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                UserProfileRequest request = objectMapper.readValue(userUpdateRequestJson, UserProfileRequest.class);
                mypageService.updateUserProfile(user, request);
            }

            if (profileImage != null && !profileImage.isEmpty()) {
                String newImageUrl = userService.updateUserProfileImage(user, profileImage);
                user.setProfileImageUrl(newImageUrl);
            }

            // 메시 스코어 계산
            meshScoreService.calculateAndSaveMeshScore(user.getUserId());

            return ResponseEntity.ok(BasicResponse.ofSuccess("프로필 수정 및 이미지 업데이트 완료"));
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.JSON_PARSING_ERROR));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.FILE_PROCESSING_ERROR));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.DUPLICATE_NICKNAME));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.UNKNOWN_ERROR));
        }
    }

}