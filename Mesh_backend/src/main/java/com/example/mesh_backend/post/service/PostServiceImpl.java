package com.example.mesh_backend.post.service;

import com.example.mesh_backend.common.CustomErrorException;
import com.example.mesh_backend.common.CustomException;
import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.common.utils.S3Uploader;
import com.example.mesh_backend.message.ErrorResponse;
import com.example.mesh_backend.post.dto.*;
import com.example.mesh_backend.post.entity.*;
import com.example.mesh_backend.post.repository.*;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PMMatchRepository pmMatchRepository;
    private final BackMatchRepository backMatchRepository;
    private final FrontMatchRepository frontMatchRepository;
    private final DesignMatchRepository designMatchRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    public PostServiceImpl(PostRepository postRepository,
                           PMMatchRepository pmMatchRepository,
                           BackMatchRepository backMatchRepository,
                           FrontMatchRepository frontMatchRepository,
                           DesignMatchRepository designMatchRepository,
                           UserRepository userRepository,
                           S3Uploader s3Uploader) {
        this.postRepository = postRepository;
        this.pmMatchRepository = pmMatchRepository;
        this.backMatchRepository = backMatchRepository;
        this.frontMatchRepository = frontMatchRepository;
        this.userRepository = userRepository;
        this.designMatchRepository = designMatchRepository;
        this.s3Uploader = s3Uploader;
    }

    @Override
    public String createPost(MultipartFile projectFile, MultipartFile projectImage, PostRequestDTO postRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        try {
            // 파일과 이미지 업로드 후 S3 URL 반환
            String projectFileUrl = (projectFile != null) ? s3Uploader.uploadFiles(projectFile, "post/files") : null;
            String projectImageUrl = (projectImage != null) ? s3Uploader.uploadFiles(projectImage, "post/images") : null;

            // DTO에 업로드된 파일 URL 추가
            postRequestDTO.setProjectFile(projectFileUrl);
            postRequestDTO.setProjectImage(projectImageUrl);

            // Post 객체 생성 및 데이터 저장
            Post post = new Post();
            post.setPostTitle(postRequestDTO.getProjectTitle());
            post.setPostContents(postRequestDTO.getProjectContents());
            post.setProjectFile(projectFileUrl); // S3 URL 저장
            post.setProjectImageUrl(projectImageUrl); // S3 URL 저장
            post.setDeadline(postRequestDTO.getDeadline());
            post.setPmBest(postRequestDTO.getPmBest());
            post.setDesignBest(postRequestDTO.getDesignBest());
            post.setBackBest(postRequestDTO.getBackBest());
            post.setFrontBest(postRequestDTO.getFrontBest());
            post.setPmCategory(postRequestDTO.getPmCategory());
            post.setDesignCategory(postRequestDTO.getDesignCategory());
            post.setBackCategory(postRequestDTO.getBackCategory());
            post.setFrontCategory(postRequestDTO.getFrontCategory());
            post.setStatus(postRequestDTO.getStatus());
            post.setCreateAt(LocalDate.now());
            post.setUser(user);

            postRepository.save(post);
            return "공고가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }
    @Override
    public List<PostResponseDTO> getTop5Projects() {
        List<Post> top5Posts = postRepository.findTop5ByOrderByViewsDesc(); // 조회수 기준 상위 5개
        return top5Posts.stream()
                .map(post -> new PostResponseDTO(post.getPostTitle()))
                .collect(Collectors.toList());
    }
    @Override
    public String updateProject(Long projectId, ProjectUpdateRequestDTO requestDTO) {
        // 1. 공고 조회
        Post post = postRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 2. 공고 정보 수정
        if (requestDTO.getPostRequest() != null) {
            PostRequestDTO postRequest = requestDTO.getPostRequest();
            post.setPostTitle(postRequest.getProjectTitle());
            post.setPostContents(postRequest.getProjectContents());
            post.setDeadline(postRequest.getDeadline());
            post.setPmBest(postRequest.getPmBest());
            post.setDesignBest(postRequest.getDesignBest());
            post.setBackBest(postRequest.getBackBest());
            post.setFrontBest(postRequest.getFrontBest());
            post.setPmCategory(postRequest.getPmCategory());
            post.setDesignCategory(postRequest.getDesignCategory());
            post.setBackCategory(postRequest.getBackCategory());
            post.setFrontCategory(postRequest.getFrontCategory());
            post.setStatus(postRequest.getStatus());
        }

        // 3. 파일 및 이미지 수정
        if (requestDTO.getProjectFile() != null) {
            post.setProjectFile(requestDTO.getProjectFile());
        }
        if (requestDTO.getProjectImage() != null) {
            post.setProjectImageUrl(requestDTO.getProjectImage());
        }

        postRepository.save(post);

        // 4. 기존 매칭 데이터 삭제
        pmMatchRepository.deleteAllByPost(post);
        backMatchRepository.deleteAllByPost(post);
        frontMatchRepository.deleteAllByPost(post);
        designMatchRepository.deleteAllByPost(post);

        // 5. 새로운 팀원 매칭 데이터 추가
        if (requestDTO.getTeamMembers() != null) {
            TeamMembersDTO teamMembers = requestDTO.getTeamMembers();

            // PM 매칭 저장
            if (teamMembers.getPmMembers() != null) {
                for (PMMemberDTO pm : teamMembers.getPmMembers()) {
                    PMMatch pmMatch = new PMMatch();
                    pmMatch.setPmId(pm.getPmId());
                    pmMatch.setPost(post);
                    pmMatchRepository.save(pmMatch);
                }
            }

            // Back 매칭 저장
            if (teamMembers.getBackMembers() != null) {
                for (BackMemberDTO back : teamMembers.getBackMembers()) {
                    BackMatch backMatch = new BackMatch();
                    backMatch.setBackId(back.getBackId());
                    backMatch.setPost(post);
                    backMatchRepository.save(backMatch);
                }
            }

            // Front 매칭 저장
            if (teamMembers.getFrontMembers() != null) {
                for (FrontMemberDTO front : teamMembers.getFrontMembers()) {
                    FrontMatch frontMatch = new FrontMatch();
                    frontMatch.setFrontId(front.getFrontId());
                    frontMatch.setPost(post);
                    frontMatchRepository.save(frontMatch);
                }
            }

            // Design 매칭 저장
            if (teamMembers.getDesignMembers() != null) {
                for (DesignMemberDTO design : teamMembers.getDesignMembers()) {
                    DesignMatch designMatch = new DesignMatch();
                    designMatch.setDesignId(design.getDesignId());
                    designMatch.setPost(post);
                    designMatchRepository.save(designMatch);
                }
            }
        }

        return "프로젝트가 성공적으로 수정되었습니다.";
    }

}
