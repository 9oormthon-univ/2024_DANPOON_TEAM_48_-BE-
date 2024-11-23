package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    void deleteAllByPost(Post post);
    List<Project> findByPost(Post post);
    List<Project> findAllByPost_PostId(Long postId);

    List<Project> findByUser_UserId(Long userId);
}
