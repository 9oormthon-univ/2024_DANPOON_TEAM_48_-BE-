package com.example.mesh_backend.mypage.controller;

import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.common.utils.S3Uploader;
import com.example.mesh_backend.login.entity.Subcategory;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import com.example.mesh_backend.login.security.CustomUserDetails;
import com.example.mesh_backend.login.service.UserService;
import com.example.mesh_backend.message.BasicResponse;
import com.example.mesh_backend.mypage.dto.response.ProjectResponseDTO;
import com.example.mesh_backend.mypage.dto.request.AwardRequest;
import com.example.mesh_backend.mypage.dto.request.CareerRequest;
import com.example.mesh_backend.mypage.dto.request.ToolRequest;
import com.example.mesh_backend.mypage.dto.request.UserProfileRequest;
import com.example.mesh_backend.mypage.dto.response.*;
import com.example.mesh_backend.mypage.entity.Award;
import com.example.mesh_backend.mypage.entity.Career;
import com.example.mesh_backend.mypage.repository.AwardRepository;
import com.example.mesh_backend.mypage.repository.CareerRepository;
import com.example.mesh_backend.mypage.service.MeshScoreService;
import com.example.mesh_backend.mypage.service.MypageProjectService;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final MypageProjectService mypageProjectService;
    private final S3Uploader s3Uploader;
    private final UserRepository userRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;

    //1. 내 정보 수정
    @PatchMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 수정 및 이미지 업데이트", description = "내 정보와 프로필 이미지를 수정하는 API")
    public ResponseEntity<BasicResponse<UserProfileLessResponse>> updateProfile(
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
            UserProfileLessResponse response = new UserProfileLessResponse(
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

    //1. 마이페이지 -  수상 추가
    @Transactional
    @PostMapping(value = "/profile/award", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "mypage - 사용자 Award 추가", description = "mypage - 사용자의 수상 이력 및 관련 증명서를 추가하는 API")
    public ResponseEntity<BasicResponse<AwardResponse>> addAward(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("awardRequest") String awardRequestJson,
            @RequestPart(value = "certificateFiles", required = false) List<MultipartFile> certificateFiles) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        User user = userRepository.findById(customUserDetails.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AwardRequest awardRequest = objectMapper.readValue(awardRequestJson, AwardRequest.class);

            Award newAward = new Award();
            newAward.setUser(user);
            newAward.setProjectName(awardRequest.getProjectName());
            newAward.setScale(awardRequest.getScale());

            // S3 파일 업로드 및 URL 설정
            if (certificateFiles != null && !certificateFiles.isEmpty()) {
                System.out.println("업로드된 파일 개수: " + certificateFiles.size()); // 디버깅
                for (MultipartFile file : certificateFiles) {
                    System.out.println("업로드 중인 파일: " + file.getOriginalFilename()); // 디버깅
                    String s3Path = "awards/" + user.getUserId();
                    String certificateUrl = s3Uploader.uploadFiles(file, s3Path);

                    if (certificateUrl == null || certificateUrl.isEmpty()) {
                        throw new RuntimeException("S3 업로드 실패");
                    }

                    System.out.println("업로드된 파일 URL: " + certificateUrl); // 디버깅
                    newAward.addCertificateUrl(certificateUrl); // 다중 URL 처리
                }
            }

            // Award 추가 및 저장
            user.getAwards().add(newAward);
            awardRepository.save(newAward); // 명시적으로 저장

            return ResponseEntity.ok(BasicResponse.ofSuccess(new AwardResponse(
                    newAward.getProjectName(),
                    newAward.getCertificateUrls(),
                    newAward.getScale()
            )));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.JSON_PARSING_ERROR));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.UNKNOWN_ERROR));
        }
    }


    //2. 마이페이지 - 커리어 추가
    @Transactional
    @PostMapping(value = "/profile/career", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "사용자 Career 추가", description = "사용자의 경력 정보를 추가하는 API")
    public ResponseEntity<BasicResponse<CareerResponse>> addCareer(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CareerRequest careerRequest) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        // User 조회
        User user = userRepository.findById(customUserDetails.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        try {
            // Career 객체 생성 및 저장
            Career newCareer = new Career();
            newCareer.setUser(user);
            newCareer.setDuration(careerRequest.getDuration());
            newCareer.setCareerContent(careerRequest.getCareerContent());

            // 기존 경력 리스트에 추가
            user.getCareers().add(newCareer);

            // 명시적으로 Career 저장
            careerRepository.save(newCareer);

            return ResponseEntity.ok(BasicResponse.ofSuccess(new CareerResponse(
                    newCareer.getDuration(),
                    newCareer.getCareerContent()
            )));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.UNKNOWN_ERROR));
        }
    }

    //마이페이지 -  수상 수정
    @Transactional
    @PatchMapping(value = "/profile/award/{awardId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "mypage - 사용자 Award 수정", description = "mypage - 사용자의 수상 이력 및 관련 증명서를 수정하는 API")
    public ResponseEntity<BasicResponse<AwardResponse>> updateAward(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long awardId,
            @RequestPart(value = "awardRequest", required = false) String awardRequestJson,
            @RequestPart(value = "certificateFiles", required = false) List<MultipartFile> certificateFiles) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        User user = userRepository.findById(customUserDetails.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Award existingAward = awardRepository.findById(awardId)
                .orElseThrow(() -> new IllegalArgumentException("Award not found"));

        if (!existingAward.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BasicResponse.ofError(ErrorCode.ACCESS_DENIED));
        }

        try {
            // JSON 요청 파싱 및 업데이트
            if (awardRequestJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                AwardRequest awardRequest = objectMapper.readValue(awardRequestJson, AwardRequest.class);

                if (awardRequest.getProjectName() != null) {
                    existingAward.setProjectName(awardRequest.getProjectName());
                }
                if (awardRequest.getScale() != null) {
                    existingAward.setScale(awardRequest.getScale());
                }
            }

            // S3 파일 업로드 및 URL 추가
            if (certificateFiles != null && !certificateFiles.isEmpty()) {
                for (MultipartFile file : certificateFiles) {
                    String s3Path = "awards/" + user.getUserId();
                    String certificateUrl = s3Uploader.uploadFiles(file, s3Path);
                    existingAward.addCertificateUrl(certificateUrl); // URL 추가
                }
            }

            // 변경 사항 저장
            awardRepository.save(existingAward);

            return ResponseEntity.ok(BasicResponse.ofSuccess(new AwardResponse(
                    existingAward.getProjectName(),
                    existingAward.getCertificateUrls(),
                    existingAward.getScale()
            )));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.JSON_PARSING_ERROR));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BasicResponse.ofError(ErrorCode.UNKNOWN_ERROR));
        }
    }


    // 마이페이지 - 커리어 수정
    @Transactional
    @PatchMapping(value = "/profile/career/{careerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "mypage - 사용자 Career 수정", description = "mypage - 사용자의 경력 정보를 수정하는 API")
    public ResponseEntity<BasicResponse<CareerResponse>> updateCareer(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long careerId,
            @RequestBody CareerRequest careerRequest) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        // User와 Career 조회
        User user = userRepository.findById(customUserDetails.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Career existingCareer = careerRepository.findById(careerId)
                .orElseThrow(() -> new IllegalArgumentException("Career not found"));

        if (!existingCareer.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(BasicResponse.ofError(ErrorCode.ACCESS_DENIED));
        }

        try {
            // 필드 업데이트
            if (careerRequest.getDuration() != null) {
                existingCareer.setDuration(careerRequest.getDuration());
            }
            if (careerRequest.getCareerContent() != null) {
                existingCareer.setCareerContent(careerRequest.getCareerContent());
            }

            // 변경 사항 저장
            careerRepository.save(existingCareer);

            return ResponseEntity.ok(BasicResponse.ofSuccess(new CareerResponse(
                    existingCareer.getDuration(),
                    existingCareer.getCareerContent()
            )));
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

    // 3. 프로젝트 목록 조회
    @GetMapping("/projects")
    @Operation(
            summary = "프로젝트 목록 조회",
            description = "사용자가 참여한 모든 프로젝트를 조회합니다."
    )
    public ResponseEntity<List<ProjectResponseDTO>> getUserProjects(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails == null) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = customUserDetails.getUser().getUserId();
        List<ProjectResponseDTO> projects = mypageProjectService.getUserProjects(userId);

        return ResponseEntity.ok(projects);
    }

    // 4. 프로젝트 목록 상세 조회
    @GetMapping("/projects/{project_id}")
    @Operation(summary = "프로젝트 상세 조회", description = "특정 프로젝트의 상세 정보를 조회하는 API")
    public ResponseEntity<BasicResponse<ProjectDetailResponse>> getProjectDetail(
            @PathVariable("project_id") Long projectId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.badRequest().body(BasicResponse.ofError(ErrorCode.UNAUTHORIZED_USER));
        }

        Long userId = customUserDetails.getUser().getUserId();
        ProjectDetailResponse response = mypageProjectService.getProjectDetail(projectId, userId);

        return ResponseEntity.ok(BasicResponse.ofSuccess(response));
    }

}