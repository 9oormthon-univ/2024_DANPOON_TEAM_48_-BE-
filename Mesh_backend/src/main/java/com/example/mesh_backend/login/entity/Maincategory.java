package com.example.mesh_backend.login.entity;

import jakarta.persistence.*;
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
public class Maincategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maincategory_id")
    private Long maincategoryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "maincategory_name")
    @Enumerated(EnumType.STRING)
    private MainCategoryName maincategoryName;

    @OneToMany(mappedBy = "maincategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subcategory> subcategories;
}