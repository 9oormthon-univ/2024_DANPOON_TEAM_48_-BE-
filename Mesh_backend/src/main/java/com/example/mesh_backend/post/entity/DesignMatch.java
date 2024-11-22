package com.example.mesh_backend.post.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "design_match")
@Data
public class DesignMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    private Long designId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Post post;
}
