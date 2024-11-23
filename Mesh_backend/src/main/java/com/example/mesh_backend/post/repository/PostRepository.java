package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p ORDER BY p.views DESC")
    List<Post> findTop5ByOrderByViewsDesc();
    Optional<Post> findById(Long postId);
    List<Post> findByUser_UserId(Long userId);

    List<Post> findByPostTitleContainingOrPostContentsContainingOrUser_NicknameContaining(String postTitle, String postContents, String nickname);

}

