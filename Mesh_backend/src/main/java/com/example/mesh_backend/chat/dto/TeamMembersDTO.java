package com.example.mesh_backend.chat.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeamMembersDTO {
    private List<MemberDTO> pmMembers;
    private List<MemberDTO> backMembers;
    private List<MemberDTO> frontMembers;
    private List<MemberDTO> designMembers;
}
