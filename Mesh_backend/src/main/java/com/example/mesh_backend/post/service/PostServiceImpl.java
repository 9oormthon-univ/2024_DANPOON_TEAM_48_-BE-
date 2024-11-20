package com.example.mesh_backend.post.service;

import com.example.mesh_backend.post.dto.PostRequestDTO;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import com.example.mesh_backend.post.service.PostService;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String createPost(PostRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Post post = new Post();
        post.setProjectTitle(requestDTO.getProjectTitle());
        post.setProjectContents(requestDTO.getProjectContents());
        post.setProjectFile(requestDTO.getProjectFile());
        post.setDeadline(requestDTO.getDeadline());
        post.setPmBest(requestDTO.getPmBest());
        post.setDesignBest(requestDTO.getDesignBest());
        post.setBackBest(requestDTO.getBackBest());
        post.setFrontBest(requestDTO.getFrontBest());
        post.setPmCategory(requestDTO.getPmCategory());
        post.setDesignCategory(requestDTO.getDesignCategory());
        post.setBackCategory(requestDTO.getBackCategory());
        post.setFrontCategory(requestDTO.getFrontCategory());
        post.setStatus(requestDTO.getStatus());
        post.setCreateAt(LocalDate.now());
        post.setUser(user);

        postRepository.save(post);
        return "공고가 성공적으로 저장되었습니다.";
    }
}
