package com.example.mesh_backend.chat.dto.request;

import lombok.Data;

@Data
public class MessageRequest {
    private Long userId;
    private Long chatRoomId;
    private String content;
}
