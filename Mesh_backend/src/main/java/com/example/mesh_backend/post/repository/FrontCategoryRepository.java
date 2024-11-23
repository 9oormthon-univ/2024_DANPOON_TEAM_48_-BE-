package com.example.mesh_backend.post.repository;
import com.example.mesh_backend.post.entity.FrontCategory;
import com.example.mesh_backend.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrontCategoryRepository extends JpaRepository<FrontCategory, Long> {
    void deleteAllByPost(Post post);
}
