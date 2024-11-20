package com.example.mesh_backend.rank.repository;

import com.example.mesh_backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankRepository extends JpaRepository<User, Long> {

    // mesh_score를 기준으로 정렬하여 상위 100명 조회
    @Query("SELECT u FROM User u ORDER BY u.meshScore DESC")
    List<User> findTop100ByOrderByMeshScoreDesc();
    int countByMeshScoreGreaterThan(Long meshScore);
}
