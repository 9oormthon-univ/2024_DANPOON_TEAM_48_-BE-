package com.example.mesh_backend.post.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "back_match")
@Data
public class BackMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    private Long backId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Post post;
}
