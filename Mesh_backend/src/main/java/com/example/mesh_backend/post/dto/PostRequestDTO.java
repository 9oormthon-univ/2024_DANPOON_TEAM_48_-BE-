package com.example.mesh_backend.post.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PostRequestDTO {
    private String projectTitle;
    private String projectContents;
    private String projectFile;
    private LocalDate deadline;
    private String pmBest;
    private String designBest;
    private String backBest;
    private String frontBest;
    private String pmCategory;
    private String designCategory;
    private String backCategory;
    private String frontCategory;
    private String status;
}
