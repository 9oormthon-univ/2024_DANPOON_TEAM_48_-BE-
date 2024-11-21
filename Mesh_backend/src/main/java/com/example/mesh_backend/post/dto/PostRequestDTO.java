package com.example.mesh_backend.post.dto;

import com.example.mesh_backend.post.entity.Status;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class PostRequestDTO {
    private String projectTitle;
    private String projectContents;
    private LocalDate deadline;
    private String pmBest;
    private String designBest;
    private String backBest;
    private String frontBest;
    private String pmCategory;
    private String designCategory;
    private String backCategory;
    private String frontCategory;
    private Status status;
    private String projectFile;
    private String projectImage;
}
