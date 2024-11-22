package com.example.mesh_backend.apply.repository;

import com.example.mesh_backend.apply.entity.Apply;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
    List<Apply> findByPost(Post post);
    List<Apply> findByUser(User user);
}
