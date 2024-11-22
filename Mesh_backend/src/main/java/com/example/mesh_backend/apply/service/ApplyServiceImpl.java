package com.example.mesh_backend.apply.service;

import com.example.mesh_backend.apply.dto.ApplyRequestDTO;
import com.example.mesh_backend.apply.entity.Apply;
import com.example.mesh_backend.apply.repository.ApplyRepository;
import com.example.mesh_backend.common.CustomException;
import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ApplyServiceImpl implements ApplyService {

    private final ApplyRepository applyRepository;
    private final PostRepository postRepository;

    public ApplyServiceImpl(ApplyRepository applyRepository, PostRepository postRepository) {
        this.applyRepository = applyRepository;
        this.postRepository = postRepository;
    }

    @Override
    public String applyToProject(User user, Long projectId, ApplyRequestDTO applyRequestDTO) {
        // Post 객체 조회
        Post post = postRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // Apply 객체 생성 및 저장
        Apply apply = new Apply();
        apply.setUser(user);
        apply.setPost(post);
        apply.setPart(applyRequestDTO.getPart());
        apply.setContents(applyRequestDTO.getContents());

        apply.setLeaderId(post.getUser().getUserId());

        applyRepository.save(apply);
        return "지원이 완료되었습니다.";
    }
}
