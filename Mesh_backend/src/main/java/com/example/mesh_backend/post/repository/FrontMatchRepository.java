package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.FrontMatch;
import com.example.mesh_backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrontMatchRepository extends JpaRepository<FrontMatch, Long> {
    void deleteAllByPost(Post post);
}
