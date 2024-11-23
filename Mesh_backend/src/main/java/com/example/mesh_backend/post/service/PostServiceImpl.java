package com.example.mesh_backend.post.service;

import com.example.mesh_backend.chat.service.ChatService;
import com.example.mesh_backend.common.CustomException;
import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.common.utils.S3Uploader;
import com.example.mesh_backend.mark.repository.MarkRepository;
import com.example.mesh_backend.post.dto.*;
import com.example.mesh_backend.post.entity.*;
import com.example.mesh_backend.post.repository.*;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PMMatchRepository pmMatchRepository;
    private final BackMatchRepository backMatchRepository;
    private final FrontMatchRepository frontMatchRepository;
    private final DesignMatchRepository designMatchRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PMCategoryRepository pmCategoryRepository;
    private final DesignCategoryRepository designCategoryRepository;
    private final BackCategoryRepository backCategoryRepository;
    private final FrontCategoryRepository frontCategoryRepository;
    private final MarkRepository markRepository;

    private final S3Uploader s3Uploader;
    private final ChatService chatService;

    public PostServiceImpl(PostRepository postRepository,
                           PMMatchRepository pmMatchRepository,
                           BackMatchRepository backMatchRepository,
                           FrontMatchRepository frontMatchRepository,
                           DesignMatchRepository designMatchRepository,
                           PMCategoryRepository pmCategoryRepository,
                           DesignCategoryRepository designCategoryRepository,
                           BackCategoryRepository backCategoryRepository,
                           FrontCategoryRepository frontCategoryRepository,
                           UserRepository userRepository,
                           ProjectRepository projectRepository,
                           MarkRepository markRepository,

                           S3Uploader s3Uploader,
                           ChatService chatService) {
        this.postRepository = postRepository;
        this.pmCategoryRepository = pmCategoryRepository;
        this.designCategoryRepository = designCategoryRepository;
        this.backCategoryRepository = backCategoryRepository;
        this.frontCategoryRepository = frontCategoryRepository;
        this.pmMatchRepository = pmMatchRepository;
        this.backMatchRepository = backMatchRepository;
        this.frontMatchRepository = frontMatchRepository;
        this.userRepository = userRepository;
        this.designMatchRepository = designMatchRepository;
        this.projectRepository = projectRepository;
        this.s3Uploader = s3Uploader;
        this.markRepository = markRepository;
        this.chatService = chatService;
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
            post.setStatus(postRequestDTO.getStatus());
            post.setCreateAt(LocalDate.now());
            post.setUser(user);

            postRepository.save(post);

            saveCategories(post, postRequestDTO);

            return "공고가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }
    private void saveCategories(Post post, PostRequestDTO postRequestDTO) {
        // PM 카테고리 저장
        if (postRequestDTO.getPmCategory() != null) {
            List<PMCategory> pmCategories = postRequestDTO.getPmCategory().stream()
                    .map(keyword -> new PMCategory(post, keyword))
                    .collect(Collectors.toList());
            pmCategoryRepository.saveAll(pmCategories);
        }

        // Design 카테고리 저장
        if (postRequestDTO.getDesignCategory() != null) {
            List<DesignCategory> designCategories = postRequestDTO.getDesignCategory().stream()
                    .map(keyword -> new DesignCategory(post, keyword))
                    .collect(Collectors.toList());
            designCategoryRepository.saveAll(designCategories);
        }

        // Back 카테고리 저장
        if (postRequestDTO.getBackCategory() != null) {
            List<BackCategory> backCategories = postRequestDTO.getBackCategory().stream()
                    .map(keyword -> new BackCategory(post, keyword))
                    .collect(Collectors.toList());
            backCategoryRepository.saveAll(backCategories);
        }

        // Front 카테고리 저장
        if (postRequestDTO.getFrontCategory() != null) {
            List<FrontCategory> frontCategories = postRequestDTO.getFrontCategory().stream()
                    .map(keyword -> new FrontCategory(post, keyword))
                    .collect(Collectors.toList());
            frontCategoryRepository.saveAll(frontCategories);
        }
    }
    @Override
    public List<PostResponseDTO> getTop5ProjectsByViews(Long userId) {
        List<Post> topPosts = postRepository.findTop5ByOrderByViewsDesc();

        // DTO로 변환하며 북마크 여부 포함
        return topPosts.stream()
                .map(post -> new PostResponseDTO(
                        post.getPostId(),
                        post.getPostTitle(),
                        post.getStatus(),
                        post.getViews(),
                        post.getProjectImageUrl(),
                        post.getUser().getNickname(),
                        post.getUser().getProfileImageUrl(),
                        isBookmarkedByUser(userId, post.getPostId()), // 북마크 여부 확인
                        post.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ))
                .collect(Collectors.toList());
    }

    private boolean isBookmarkedByUser(Long userId, Long postId) {
        return markRepository.existsByUser_UserIdAndPost_PostId(userId, postId);
    }
    @Transactional
    @Override
    public String updateProject(Long projectId, ProjectUpdateRequestDTO requestDTO, Long userId) {
        // 1. 공고 조회
        Post post = postRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 1-1. 수정 권한 확인
        if (!post.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        Status previousStatus = post.getStatus();

        // 2. 공고 정보 수정
        if (requestDTO.getPostRequest() != null) {
            PostRequestDTO postRequest = requestDTO.getPostRequest();
            updatePostDetails(post, postRequest);
        }

        // 3. 파일 처리
        updatePostFiles(post, requestDTO);

        // 4. 기존 매칭 데이터 삭제
        pmMatchRepository.deleteAllByPost(post);
        backMatchRepository.deleteAllByPost(post);
        frontMatchRepository.deleteAllByPost(post);
        designMatchRepository.deleteAllByPost(post);

        // 5. 기존 프로젝트 데이터 삭제
        projectRepository.deleteAllByPost(post);

        // 6. 새로운 매칭 및 프로젝트 데이터 추가
        if (requestDTO.getTeamMembers() != null) {
            TeamMembersDTO teamMembers = requestDTO.getTeamMembers();

            // 팀장 추가
            addTeamLeaderToProject(post);

            // 팀원 추가
            addTeamMembersToProject(teamMembers, post);
        }

        // 7. 모집완료 상태 확인 후 팀 채팅방 생성
        if (previousStatus != Status.모집완료 && post.getStatus() == Status.모집완료) {
            // 저장된 Project 엔터티를 기반으로 팀 멤버를 가져오기
            List<User> teamMembers = projectRepository.findByPost(post).stream()
                    .map(Project::getUser)
                    .collect(Collectors.toList());

            // 팀 채팅방 생성
            chatService.createTeamChatRoom(post, teamMembers);
        }
        // 8. 기존 카테고리 삭제 후 저장
        deleteCategories(post);
        saveCategories(post, requestDTO.getPostRequest());

        // 상태 업데이트
        if (requestDTO.getPostRequest() != null && requestDTO.getPostRequest().getStatus() != null) {
            post.setStatus(requestDTO.getPostRequest().getStatus());

            // 활동 종료 시 D_day를 5로 설정
            if (post.getStatus() == Status.활동완료) {
                post.setDDay(5);
            }
        }


        postRepository.save(post);
        return "프로젝트가 성공적으로 수정되었습니다.";
    }

    private void deleteCategories(Post post) {
        pmCategoryRepository.deleteAllByPost(post);
        designCategoryRepository.deleteAllByPost(post);
        backCategoryRepository.deleteAllByPost(post);
        frontCategoryRepository.deleteAllByPost(post);
    }

    private void updatePostDetails(Post post, PostRequestDTO postRequest) {
        if (postRequest.getProjectTitle() != null) post.setPostTitle(postRequest.getProjectTitle());
        if (postRequest.getProjectContents() != null) post.setPostContents(postRequest.getProjectContents());
        if (postRequest.getDeadline() != null) post.setDeadline(postRequest.getDeadline());
        if (postRequest.getPmBest() != null) post.setPmBest(postRequest.getPmBest());
        if (postRequest.getDesignBest() != null) post.setDesignBest(postRequest.getDesignBest());
        if (postRequest.getBackBest() != null) post.setBackBest(postRequest.getBackBest());
        if (postRequest.getFrontBest() != null) post.setFrontBest(postRequest.getFrontBest());
        if (postRequest.getStatus() != null) post.setStatus(postRequest.getStatus());
    }

    private void updatePostFiles(Post post, ProjectUpdateRequestDTO requestDTO) {
        if (requestDTO.getProjectFile() != null) post.setProjectFile(requestDTO.getProjectFile());
        if (requestDTO.getProjectImage() != null) post.setProjectImageUrl(requestDTO.getProjectImage());
    }

    private void addTeamLeaderToProject(Post post) {
        Project teamLeaderProject = new Project();
        teamLeaderProject.setPost(post);
        teamLeaderProject.setUser(post.getUser());
        teamLeaderProject.setRole("팀장");
        teamLeaderProject.setStatus(post.getStatus().toString());
        projectRepository.save(teamLeaderProject);
    }

    private void addTeamMembersToProject(TeamMembersDTO teamMembers, Post post) {
        if (teamMembers.getPmMembers() != null) {
            for (PMMemberDTO pm : teamMembers.getPmMembers()) {
                saveMemberToProject(pm.getNickname(), post, "팀원");
            }
        }
        if (teamMembers.getBackMembers() != null) {
            for (BackMemberDTO back : teamMembers.getBackMembers()) {
                saveMemberToProject(back.getNickname(), post, "팀원");
            }
        }
        if (teamMembers.getFrontMembers() != null) {
            for (FrontMemberDTO front : teamMembers.getFrontMembers()) {
                saveMemberToProject(front.getNickname(), post, "팀원");
            }
        }
        if (teamMembers.getDesignMembers() != null) {
            for (DesignMemberDTO design : teamMembers.getDesignMembers()) {
                saveMemberToProject(design.getNickname(), post, "팀원");
            }
        }
    }


    private void saveMemberToProject(String nickname, Post post, String role) {

        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        Project project = new Project();
        project.setPost(post);
        project.setUser(user);
        project.setRole(role);
        project.setStatus(post.getStatus().toString());
        projectRepository.save(project);
    }
}