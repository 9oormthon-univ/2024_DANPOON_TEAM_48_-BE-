package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p ORDER BY p.views DESC")
    List<Post> findTop5ByOrderByViewsDesc();
    Optional<Post> findById(Long postId);
    List<Post> findByUser_UserId(Long userId);

    List<Post> findByPostTitleContainingOrPostContentsContainingOrUser_NicknameContaining(String postTitle, String postContents, String nickname);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN PMCategory pm ON pm.post = p " +
            "LEFT JOIN DesignCategory d ON d.post = p " +
            "LEFT JOIN BackCategory b ON b.post = p " +
            "LEFT JOIN FrontCategory f ON f.post = p " +
            "WHERE p.postTitle LIKE %:keyword% " +
            "OR p.postContents LIKE %:keyword% " +
            "OR p.user.nickname LIKE %:keyword% " +
            "OR pm.keyword LIKE %:keyword% " +
            "OR d.keyword LIKE %:keyword% " +
            "OR b.keyword LIKE %:keyword% " +
            "OR f.keyword LIKE %:keyword%")
    List<Post> searchByKeyword(@Param("keyword") String keyword);
    List<Post> findAllBydDayGreaterThan(int dDay);

    List<Post> findByProjects_ProjectId(Long projectId);
}

