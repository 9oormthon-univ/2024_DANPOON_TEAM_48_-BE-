package com.example.mesh_backend.login.repository;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.entity.Refreshtoken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshtokenRepository extends JpaRepository<Refreshtoken, Long> {
    Optional<Refreshtoken> findByRefreshToken(String refreshToken);
    Refreshtoken findByUser(User user);
}