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
    @Transactional
    @Override
    public String updateProject(Long projectId, ProjectUpdateRequestDTO requestDTO) {
        // 1. 공고 조회
        Post post = postRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 2. 공고 정보 수정
        if (requestDTO.getPostRequest() != null) {
            PostRequestDTO postRequest = requestDTO.getPostRequest();
            if (postRequest.getProjectTitle() != null) {
                post.setPostTitle(postRequest.getProjectTitle());
            }
            if (postRequest.getProjectContents() != null) {
                post.setPostContents(postRequest.getProjectContents());
            }
            if (postRequest.getDeadline() != null) {
                post.setDeadline(postRequest.getDeadline());
            }
            if (postRequest.getPmBest() != null) {
                post.setPmBest(postRequest.getPmBest());
            }
            if (postRequest.getDesignBest() != null) {
                post.setDesignBest(postRequest.getDesignBest());
            }
            if (postRequest.getBackBest() != null) {
                post.setBackBest(postRequest.getBackBest());
            }
            if (postRequest.getFrontBest() != null) {
                post.setFrontBest(postRequest.getFrontBest());
            }
            if (postRequest.getPmCategory() != null) {
                post.setPmCategory(postRequest.getPmCategory());
            }
            if (postRequest.getDesignCategory() != null) {
                post.setDesignCategory(postRequest.getDesignCategory());
            }
            if (postRequest.getBackCategory() != null) {
                post.setBackCategory(postRequest.getBackCategory());
            }
            if (postRequest.getFrontCategory() != null) {
                post.setFrontCategory(postRequest.getFrontCategory());
            }
            if (postRequest.getStatus() != null) {
                post.setStatus(postRequest.getStatus());
            }
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
                    savePMMatchByNickname(pm.getNickname(), post);
                }
            }

            // Back 매칭 저장
            if (teamMembers.getBackMembers() != null) {
                for (BackMemberDTO back : teamMembers.getBackMembers()) {
                    saveBackMatchByNickname(back.getNickname(), post);
                }
            }

            // Front 매칭 저장
            if (teamMembers.getFrontMembers() != null) {
                for (FrontMemberDTO front : teamMembers.getFrontMembers()) {
                    saveFrontMatchByNickname(front.getNickname(), post);
                }
            }

            // Design 매칭 저장
            if (teamMembers.getDesignMembers() != null) {
                for (DesignMemberDTO design : teamMembers.getDesignMembers()) {
                    saveDesignMatchByNickname(design.getNickname(), post);
                }
            }
        }

        return "프로젝트가 성공적으로 수정되었습니다.";
    }
    // Helper 메서드: nickname으로 User ID 찾고 매칭 데이터 저장
    private void savePMMatchByNickname(String nickname, Post post) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        PMMatch pmMatch = new PMMatch();
        pmMatch.setPmId(user.getUserId());
        pmMatch.setPost(post);
        pmMatchRepository.save(pmMatch);
    }
    private void saveBackMatchByNickname(String nickname, Post post) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        BackMatch backMatch = new BackMatch();
        backMatch.setBackId(user.getUserId());
        backMatch.setPost(post);
        backMatchRepository.save(backMatch);
    }

    private void saveFrontMatchByNickname(String nickname, Post post) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        FrontMatch frontMatch = new FrontMatch();
        frontMatch.setFrontId(user.getUserId());
        frontMatch.setPost(post);
        frontMatchRepository.save(frontMatch);
    }
    private void saveDesignMatchByNickname(String nickname, Post post) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        DesignMatch designMatch = new DesignMatch();
        designMatch.setDesignId(user.getUserId());
        designMatch.setPost(post);
        designMatchRepository.save(designMatch);
    }

}
