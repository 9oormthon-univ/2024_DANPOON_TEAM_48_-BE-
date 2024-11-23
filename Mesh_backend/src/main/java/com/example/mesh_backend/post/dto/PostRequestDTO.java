package com.example.mesh_backend.post.dto;

import com.example.mesh_backend.post.entity.Status;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class PostRequestDTO {
    private String projectTitle;
    private String projectContents;
    private LocalDate deadline;
    private String pmBest;
    private String designBest;
    private String backBest;
    private String frontBest;
    private List<String> pmCategory;
    private List<String> designCategory;
    private List<String> backCategory;
    private List<String> frontCategory;
    private Status status;
}
