package com.example.mesh_backend.post.dto;
import lombok.Data;

@Data
public class ProjectUpdateRequestDTO {
    private String projectFile;
    private String projectImage;
    private PostRequestDTO postRequest;
    private TeamMembersDTO teamMembers;
}
