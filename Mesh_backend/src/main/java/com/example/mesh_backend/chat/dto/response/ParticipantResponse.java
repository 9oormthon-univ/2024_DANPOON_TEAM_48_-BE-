package com.example.mesh_backend.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponse {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
}