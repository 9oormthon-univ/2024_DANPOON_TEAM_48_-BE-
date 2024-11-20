package com.example.mesh_backend.mypage.repository;

import com.example.mesh_backend.mypage.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerRepository  extends JpaRepository<Career, Long> {
}