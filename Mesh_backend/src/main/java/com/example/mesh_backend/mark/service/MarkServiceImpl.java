package com.example.mesh_backend.mark.service;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.mark.entity.Mark;
import com.example.mesh_backend.mark.repository.MarkRepository;
import com.example.mesh_backend.mypage.dto.response.ProjectResponseDTO;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarkServiceImpl implements MarkService {

    private final MarkRepository markRepository;
    private final PostRepository postRepository;

    public MarkServiceImpl(MarkRepository markRepository, PostRepository postRepository) {
        this.markRepository = markRepository;
        this.postRepository = postRepository;
    }

    @Override
    public String toggleMark(User user, Long projectId) {
        Post post = postRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        // 북마크 존재 여부 확인
        return markRepository.findByUserAndPost(user, post)
                .map(mark -> {
                    markRepository.deleteByUserAndPost(user, post);
                    return "북마크가 삭제되었습니다.";
                })
                .orElseGet(() -> {
                    Mark newMark = new Mark();
                    newMark.setUser(user);
                    newMark.setPost(post);
                    markRepository.save(newMark);
                    return "북마크가 등록되었습니다.";
                });
    }

    @Override
    public List<ProjectResponseDTO> getBookmarkedProjects(User user) {
        // 사용자의 북마크 데이터를 가져옴
        List<Mark> marks = markRepository.findAllByUser(user);

        // ProjectResponseDTO로 변환
        return marks.stream()
                .map(mark -> {
                    Post post = mark.getPost();
                    return new ProjectResponseDTO(
                            post.getPostId(),
                            post.getPostTitle(),
                            post.getStatus(),
                            post.getViews(),
                            post.getProjectImageUrl(),
                            post.getUser().getNickname(),
                            post.getUser().getProfileImageUrl(),
                            true, // 북마크 여부는 항상 true
                            post.getDDay() // D-Day 추가
                    );
                })
                .collect(Collectors.toList());
    }
}
