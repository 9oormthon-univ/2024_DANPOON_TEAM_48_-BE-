package com.example.mesh_backend.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column
    private String major;

    @Column
    private String email;

    @Column
    private String nickname; //인스타 아이디같은 공유 아이디

    @Column(name = "kakao_id")
    private Long kakaoId;  //카카오 유저 고유 ID -> 회원탈퇴 시 필요

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "mesh_score")
    private Long meshScore;

    @Column
    private String portfolio;
}