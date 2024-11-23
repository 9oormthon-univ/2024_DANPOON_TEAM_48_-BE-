package com.example.mesh_backend.mypage.service;

import com.example.mesh_backend.mypage.dto.response.ProjectResponseDTO;
import com.example.mesh_backend.mypage.dto.response.ProjectDetailResponse;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.entity.Project;
import com.example.mesh_backend.post.repository.PostRepository;
import com.example.mesh_backend.mark.repository.MarkRepository;
import com.example.mesh_backend.post.repository.ProjectRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageProjectServiceImpl implements MypageProjectService {

    private final PostRepository postRepository;
    private final MarkRepository markRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<ProjectResponseDTO> getUserProjects(Long userId) {
        // Project 테이블에서 사용자가 참여한 프로젝트 조회
        List<Project> projects = projectRepository.findByUser_UserId(userId);

        // DTO로 변환
        return projects.stream().map(project -> {
                    // Project와 연관된 Post 데이터 가져오기
                    Long projectId = project.getProjectId();
                    List<Post> posts = postRepository.findByProjects_ProjectId(projectId);

                    // 관련 Post 데이터를 DTO로 변환
                    return posts.stream().map(post -> {
                        boolean isBookmarked = markRepository.existsByUser_UserIdAndPost_PostId(userId, post.getPostId());
                        return new ProjectResponseDTO(
                                post.getPostId(),
                                post.getPostTitle(),
                                post.getStatus(),
                                post.getViews(),
                                post.getProjectImageUrl(),
                                post.getUser().getNickname(),
                                post.getUser().getProfileImageUrl(),
                                isBookmarked,
                                post.getDDay()
                        );
                    }).collect(Collectors.toList());
                }).flatMap(List::stream) // 모든 Post 리스트를 평탄화
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetail(Long projectId, Long userId) {
        Post post = postRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        boolean isMarked = markRepository.existsByUser_UserIdAndPost_PostId(userId, projectId);

        return ProjectDetailResponse.builder()
                .postId(post.getPostId())
                .postTitle(post.getPostTitle())
                .postContents(post.getPostContents())
                .projectImageUrl(post.getProjectImageUrl())
                .projectFile(post.getProjectFile())
                .views(post.getViews())
                .deadline(post.getDeadline())
                .createAt(post.getCreateAt())
                .status(post.getStatus())
                .nickname(post.getUser().getNickname())
                .profileImageUrl(post.getUser().getProfileImageUrl())
                .isMarked(isMarked)
                .build();
    }
}
