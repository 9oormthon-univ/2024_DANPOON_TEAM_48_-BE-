package com.example.mesh_backend.post.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "front_match")
@Data
public class FrontMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    private Long frontId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Post post;
}
