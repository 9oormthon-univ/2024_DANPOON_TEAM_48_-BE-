package com.example.mesh_backend.mypage.repository;

import com.example.mesh_backend.mypage.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardRepository  extends JpaRepository<Award, Long> {
}