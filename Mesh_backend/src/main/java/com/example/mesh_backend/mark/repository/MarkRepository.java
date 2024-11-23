package com.example.mesh_backend.mark.repository;

import com.example.mesh_backend.mark.entity.Mark;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);
    boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

    List<Mark> findAllByUser(User user);
}
