package com.example.mesh_backend.login.repository;

import com.example.mesh_backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    User findByNickname(String nickname);
    boolean existsByNickname(String nickname);
}