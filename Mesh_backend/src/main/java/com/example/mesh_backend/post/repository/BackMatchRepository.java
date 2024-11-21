package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.BackMatch;
import com.example.mesh_backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackMatchRepository extends JpaRepository<BackMatch, Long> {
    void deleteAllByPost(Post post);
}
