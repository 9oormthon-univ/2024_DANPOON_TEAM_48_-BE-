package com.example.mesh_backend.login.repository;

import com.example.mesh_backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(Long userId);

    boolean existsByNickname(String nickname);

    Optional<User> findByNickname(String nickname);
    Optional<User> findById(Long userId);

}