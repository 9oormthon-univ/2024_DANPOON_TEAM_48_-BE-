package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
