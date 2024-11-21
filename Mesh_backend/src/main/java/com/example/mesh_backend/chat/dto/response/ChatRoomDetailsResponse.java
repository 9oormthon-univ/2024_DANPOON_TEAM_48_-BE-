package com.example.mesh_backend.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDetailsResponse {
    private Long chatRoomId;
    private String chatRoomName;
    private String lastMessage;
    private String lastMessageSender;
    private String lastMessageSenderProfile;
    private LocalDateTime lastMessageCreatedAt;
    private long participantCount;
}
