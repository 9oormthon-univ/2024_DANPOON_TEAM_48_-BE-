package com.example.mesh_backend.post.controller;

import com.example.mesh_backend.common.CustomException;
import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.post.dto.PostRequestDTO;
import com.example.mesh_backend.post.dto.ProjectUpdateRequestDTO;
import com.example.mesh_backend.post.dto.TeamMembersDTO;
import com.example.mesh_backend.post.service.PostService;
import com.example.mesh_backend.login.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Tag(name = "팀원 모집 공고", description = "공고 관련 API")
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final TokenService tokenService;

    public PostController(PostService postService, TokenService tokenService) {
        this.postService = postService;
        this.tokenService = tokenService;
    }

    @PostMapping()
    @Operation(
            summary = "공고 등록 API",
            description = "사용자가 팀원을 모집하는 공고를 등록함."
    )
    public ResponseEntity<String> createPost(
            @RequestPart(value = "projectFile", required = false) MultipartFile projectFile,
            @RequestPart(value = "projectImage", required = false) MultipartFile projectImage,
            @RequestPart("postRequest") PostRequestDTO postRequestDTO,
            @RequestHeader("Authorization") String token
    ) {
        try {
            // "Bearer " 접두사 제거
            String accessToken = token.replace("Bearer ", "");

            // AccessToken에서 User 정보 조회
            User user = tokenService.getUserFromAccessToken(accessToken);

            // userId 추출
            Long userId = user.getUserId();

            // PostService 호출
            String message = postService.createPost(
                    projectFile, projectImage, postRequestDTO, userId
            );
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    @PatchMapping("/{project_id}")
    public ResponseEntity<String> updateProject(
            @PathVariable("project_id") Long projectId,
            @RequestPart(value = "projectFile", required = false) MultipartFile projectFile,
            @RequestPart(value = "projectImage", required = false) MultipartFile projectImage,
            @RequestPart("postRequest") PostRequestDTO postRequestDTO,
            @RequestPart("teamMembers") TeamMembersDTO teamMembers,
            @RequestHeader("Authorization") String token
    ) {
        try {
            // "Bearer " 접두사 제거
            String accessToken = token.replace("Bearer ", "");

            // AccessToken에서 User 정보 조회
            User user = tokenService.getUserFromAccessToken(accessToken);

            // userId 추출
            Long userId = user.getUserId();

            // DTO 생성 및 값 설정
            ProjectUpdateRequestDTO requestDTO = new ProjectUpdateRequestDTO();
            requestDTO.setPostRequest(postRequestDTO);
            requestDTO.setProjectFile(projectFile != null ? projectFile.getOriginalFilename() : null);
            requestDTO.setProjectImage(projectImage != null ? projectImage.getOriginalFilename() : null);
            requestDTO.setTeamMembers(teamMembers);

            // 서비스 호출
            String message = postService.updateProject(projectId, requestDTO, userId);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
