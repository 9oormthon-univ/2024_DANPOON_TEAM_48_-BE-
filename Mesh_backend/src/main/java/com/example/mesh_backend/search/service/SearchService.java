package com.example.mesh_backend.search.service;

import com.example.mesh_backend.search.dto.ProjectResponseDTO;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import com.example.mesh_backend.mark.repository.MarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final MarkRepository markRepository;

    public List<ProjectResponseDTO> searchProjects(String keyword, List<String> categories) {
        List<Post> posts = postRepository.findByPostTitleContainingOrPostContentsContainingOrUser_NicknameContaining(
                keyword, keyword, keyword
        );

        return posts.stream()
                .filter(post -> {
                    if (categories != null && !categories.isEmpty()) {
                        // 모든 카테고리에서 매칭 여부 확인
                        return categories.stream().allMatch(category ->
                                post.getPmCategories().stream().anyMatch(cat -> cat.getKeyword().equals(category)) ||
                                        post.getDesignCategories().stream().anyMatch(cat -> cat.getKeyword().equals(category)) ||
                                        post.getBackCategories().stream().anyMatch(cat -> cat.getKeyword().equals(category)) ||
                                        post.getFrontCategories().stream().anyMatch(cat -> cat.getKeyword().equals(category))
                        );
                    }
                    return true; // categories가 없으면 필터링 없이 반환
                })
                .map(post -> {
                    List<String> postCategories = collectCategories(post); // PM, Design 등 카테고리 이름만 추출
                    boolean isBookmarked = markRepository.existsByUser_UserIdAndPost_PostId(
                            post.getUser().getUserId(), post.getPostId()
                    );
                    return new ProjectResponseDTO(
                            post.getPostId(),
                            post.getPostTitle(),
                            post.getStatus().toString(),
                            post.getViews() != null ? post.getViews().intValue() : 0,
                            post.getProjectImageUrl(),
                            post.getUser().getNickname(),
                            post.getUser().getProfileImageUrl(),
                            isBookmarked,
                            postCategories
                    );
                })
                .collect(Collectors.toList());
    }

    // 특정 게시글(Post)에 카테고리 키워드가 포함되는지 확인
    private boolean postContainsCategories(Post post, List<String> categories) {
        List<String> postCategories = collectCategories(post); // 게시글의 카테고리 리스트
        for (String category : categories) {
            if (postCategories.contains(category)) {
                return true; // 포함된 경우
            }
        }
        return false; // 포함되지 않은 경우
    }

    // 게시글의 모든 카테고리를 수집하는 메서드
    private List<String> collectCategories(Post post) {
        List<String> categories = new ArrayList<>();

        if (post.getPmCategories() != null && !post.getPmCategories().isEmpty()) {
            categories.add("PM");
        }
        if (post.getDesignCategories() != null && !post.getDesignCategories().isEmpty()) {
            categories.add("Design");
        }
        if (post.getBackCategories() != null && !post.getBackCategories().isEmpty()) {
            categories.add("Back");
        }
        if (post.getFrontCategories() != null && !post.getFrontCategories().isEmpty()) {
            categories.add("Front");
        }

        return categories;
    }
}
