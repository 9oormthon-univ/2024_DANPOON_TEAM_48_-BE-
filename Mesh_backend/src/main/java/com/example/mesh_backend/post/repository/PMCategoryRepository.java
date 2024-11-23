package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.PMCategory;
import com.example.mesh_backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PMCategoryRepository extends JpaRepository<PMCategory, Long> {
    void deleteAllByPost(Post post);
}

