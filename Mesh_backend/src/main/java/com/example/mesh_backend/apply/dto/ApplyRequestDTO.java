package com.example.mesh_backend.apply.dto;

import lombok.Data;
import com.example.mesh_backend.apply.entity.Part;

@Data
public class ApplyRequestDTO {
    private Part part;   // 지원 파트
    private String contents; // 팀장에게 남길 한마디
}
