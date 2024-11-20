package com.example.mesh_backend.mypage.repository;

import com.example.mesh_backend.mypage.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<Tool, Long> {
}