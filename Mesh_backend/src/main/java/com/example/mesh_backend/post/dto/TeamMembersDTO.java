package com.example.mesh_backend.post.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeamMembersDTO {
    private List<PMMemberDTO> pmMembers;
    private List<BackMemberDTO> backMembers;
    private List<FrontMemberDTO> frontMembers;
    private List<DesignMemberDTO> designMembers;
}
