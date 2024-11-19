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
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subcategory_id")
    private Long subcategoryId;

    @ManyToOne
    @JoinColumn(name = "maincategory_id")
    private Maincategory maincategory;

    @Column(name = "subcategory_name")
    @Enumerated(EnumType.STRING)
    private SubCategoryName subcategoryName;
}