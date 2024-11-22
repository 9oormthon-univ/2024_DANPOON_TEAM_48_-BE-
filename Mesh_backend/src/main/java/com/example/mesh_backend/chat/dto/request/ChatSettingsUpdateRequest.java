package com.example.mesh_backend.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSettingsUpdateRequest {

    private String notionLink;
    private String githubLink;
    private String figmaLink;
}