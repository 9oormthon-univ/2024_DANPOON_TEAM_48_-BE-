package com.example.mesh_backend.post.service;

import com.example.mesh_backend.common.CustomErrorException;
import com.example.mesh_backend.common.CustomException;
import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.common.utils.S3Uploader;
import com.example.mesh_backend.message.ErrorResponse;
import com.example.mesh_backend.post.dto.PostRequestDTO;
import com.example.mesh_backend.post.dto.PostResponseDTO;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, S3Uploader s3Uploader) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
}
