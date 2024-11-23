package com.example.mesh_backend.search.service;

import com.example.mesh_backend.mypage.dto.ProjectResponseDTO;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import com.example.mesh_backend.mark.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final MarkRepository markRepository;

    public List<ProjectResponseDTO> searchProjects(String keyword) {
        List<Post> posts = postRepository.findByPostTitleContainingOrPostContentsContainingOrUser_NicknameContaining(keyword, keyword, keyword);

        return posts.stream().map(post -> {
            boolean isBookmarked = markRepository.existsByUser_UserIdAndPost_PostId(post.getUser().getUserId(), post.getPostId());
            return new ProjectResponseDTO(
                    post.getPostId(),
                    post.getPostTitle(),
                    post.getStatus(),
                    post.getViews(),
                    post.getProjectImageUrl(),
                    post.getUser().getNickname(),
                    post.getUser().getProfileImageUrl(),
                    isBookmarked
            );
        }).collect(Collectors.toList());
    }
}
