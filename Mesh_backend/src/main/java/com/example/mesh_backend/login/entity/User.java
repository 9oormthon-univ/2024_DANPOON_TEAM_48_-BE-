package com.example.mesh_backend.login.entity;

import com.example.mesh_backend.mypage.entity.Award;
import com.example.mesh_backend.mypage.entity.Career;
import com.example.mesh_backend.mypage.entity.Tool;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @Size(max = 100, message = "Content는 최대 100자까지 입력할 수 있습니다.")
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "mesh_score")
    private Long meshScore;

    @Column
    private String portfolio;


    @OneToMany(mappedBy = "user",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Maincategory> maincategories;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tool> tools;

    @OneToMany(mappedBy = "user",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Award> awards;

    @OneToMany(mappedBy = "user",  fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers;

}