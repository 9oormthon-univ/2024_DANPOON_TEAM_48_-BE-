package com.example.mesh_backend.apply.entity;

import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.post.entity.Post;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "apply")
@Data
@NoArgsConstructor
public class Apply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long applyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 지원자 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 지원한 게시글 정보

    @Enumerated(EnumType.STRING)
    @Column(name = "part", nullable = false)
    private Part part; // 지원 파트 (기획, 디자인, 백엔드, 프론트엔드)

    @Column(name = "contents", columnDefinition = "TEXT", nullable = false)
    private String contents; // 팀장에게 남길 한마디

    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt = LocalDateTime.now(); // 현재 시간 자동 설정

    @Column(name = "leader_id", nullable = false)
    private Long leaderId; // Leader ID 추가

}
