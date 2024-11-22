package com.example.mesh_backend.mypage.controller;

import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.login.entity.SubCategoryName;
import com.example.mesh_backend.login.entity.Subcategory;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.security.CustomUserDetails;
import com.example.mesh_backend.login.service.UserService;
import com.example.mesh_backend.message.BasicResponse;
import com.example.mesh_backend.mypage.dto.request.AwardRequest;
import com.example.mesh_backend.mypage.dto.request.CareerRequest;
import com.example.mesh_backend.mypage.dto.request.ToolRequest;
import com.example.mesh_backend.mypage.dto.request.UserProfileRequest;
import com.example.mesh_backend.mypage.dto.response.UserProfileResponse;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<BasicResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart(value = "userUpdateRequest", required = false) String userUpdateRequestJson,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        User user = customUserDetails.getUser();

        try {
            // 업데이트 요청 처리
            if (userUpdateRequestJson != null && !userUpdateRequestJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                UserProfileRequest request = objectMapper.readValue(userUpdateRequestJson, UserProfileRequest.class);
                mypageService.updateUserProfile(user, request);
            }

            // 프로필 이미지 업데이트
            if (profileImage != null && !profileImage.isEmpty()) {
                String newImageUrl = userService.updateUserProfileImage(user, profileImage);
                user.setProfileImageUrl(newImageUrl);
            }

            // 메시 스코어 계산
            meshScoreService.calculateAndSaveMeshScore(user.getUserId());

            // 응답 데이터 생성
            UserProfileResponse response = new UserProfileResponse(
                    user.getUserId(),
                    user.getNickname(),
                    user.getMeshScore(),
                    user.getProfileImageUrl(),
                    user.getMajor(),
                    user.getContent(),
                    user.getPortfolio(),
                    user.getMaincategories().stream()
                            .flatMap(mainCategory -> mainCategory.getSubcategories().stream())
                            .map(Subcategory::getSubcategoryName)
                            .collect(Collectors.toList()),
                    user.getAwards().stream()
                            .map(award -> new AwardRequest(
                                    award.getProjectName(),
                                    award.getPart(),
                                    award.getResult(),
                                    award.getScale()))
                            .collect(Collectors.toList()),
                    user.getCareers().stream()
                            .map(career -> new CareerRequest(
                                    career.getDuration(),
                                    career.getCompany(),
                                    career.getPosition()))
                            .collect(Collectors.toList()),
                    user.getTools().stream()
                            .map(tool -> new ToolRequest(
                                    tool.getToolName(),
                                    tool.getProficiency()))
                            .collect(Collectors.toList())
            );

            return ResponseEntity.ok(BasicResponse.ofSuccess(response));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.JSON_PARSING_ERROR));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.FILE_PROCESSING_ERROR));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.DUPLICATE_NICKNAME));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.UNKNOWN_ERROR));
        }
    }

    // 2. 내 프로필 조회
    @GetMapping("/my/profile")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회하는 API")
    public ResponseEntity<BasicResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        try {
            Long userId = customUserDetails.getUser().getUserId();
            UserProfileResponse response = mypageService.getUserProfile(userId);

            return ResponseEntity.ok(BasicResponse.ofSuccess(response));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.USER_NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.UNKNOWN_ERROR));
        }
    }

    // 3. 다른 사용자 프로필 조회
    @GetMapping("/{userId}/profile")
    @Operation(summary = "다른 사용자 프로필 조회", description = "다른 사용자의 프로필 정보를 조회하는 API")
    public ResponseEntity<BasicResponse<UserProfileResponse>> getOtherUserProfile(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long userId) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        try {
            // 다른 사용자의 프로필 조회
            UserProfileResponse response = mypageService.getUserProfile(userId);

            return ResponseEntity.ok(BasicResponse.ofSuccess(response));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.USER_NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.UNKNOWN_ERROR));
        }
    }



}