package com.example.mesh_backend.mypage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolRequest {
    private String toolName;
    private int proficiency;
}