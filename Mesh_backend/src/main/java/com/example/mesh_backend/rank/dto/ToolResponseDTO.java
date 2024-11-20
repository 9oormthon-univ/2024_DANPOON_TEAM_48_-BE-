package com.example.mesh_backend.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ToolResponseDTO {
    private String toolName;
    private Integer proficiency; // 1~5 점수
}
