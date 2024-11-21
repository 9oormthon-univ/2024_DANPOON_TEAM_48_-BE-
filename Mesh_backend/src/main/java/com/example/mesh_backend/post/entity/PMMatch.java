package com.example.mesh_backend.post.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pm_match")
@Data
public class PMMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    private Long pmId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Post post;

    public PMMatch() {
    }
    public PMMatch(Long pmId, Post post) {
        this.pmId = pmId;
        this.post = post;
    }
}