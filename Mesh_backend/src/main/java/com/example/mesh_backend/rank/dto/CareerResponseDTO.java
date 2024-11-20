package com.example.mesh_backend.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CareerResponseDTO {
    private String company;
    private String position;
    private String duration;
}
