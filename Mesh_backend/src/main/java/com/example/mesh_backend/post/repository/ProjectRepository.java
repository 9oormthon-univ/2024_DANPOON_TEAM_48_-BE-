package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    void deleteAllByPost(Post post);
}
