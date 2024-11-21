package com.example.mesh_backend.post.repository;

import com.example.mesh_backend.post.entity.PMMatch;
import com.example.mesh_backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PMMatchRepository extends JpaRepository<PMMatch, Long> {
    void deleteAllByPost(Post post); // 공고 기준으로 삭제
}

