package com.example.mesh_backend.post.entity;

import com.example.mesh_backend.login.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "post_title", nullable = false)
    private String postTitle;

    @Column(name = "post_contents", nullable = false)
    private String postContents;

    @Column(name = "project_image_url")
    private String projectImageUrl;

    @Column(name = "project_file")
    private String projectFile;

    @Column(name = "views", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long views = 0L;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "create_at", updatable = false)
    private LocalDate createAt;

    @Column(name = "pm_best")
    private String pmBest;

    @Column(name = "design_best")
    private String designBest;

    @Column(name = "back_best")
    private String backBest;

    @Column(name = "front_best")
    private String frontBest;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PMCategory> pmCategories;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesignCategory> designCategories;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BackCategory> backCategories;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FrontCategory> frontCategories;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    @Column(name = "d_day", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int dDay = 0;

}
