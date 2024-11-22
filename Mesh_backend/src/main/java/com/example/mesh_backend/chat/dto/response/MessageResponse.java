package com.example.mesh_backend.chat.dto.response;

import com.example.mesh_backend.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageResponse {
    private Long userId;
    private Long messageId;
    private String content;
    private String senderNickname;
    private String senderProfileUrl;
    private String attachmentUrl;
    private LocalDateTime createdAt;

    public static MessageResponse fromEntity(Message message) {
        return new MessageResponse(
                message.getUser().getUserId(),
                message.getId(),
                message.getContent(),
                message.getUser().getNickname(),
                message.getUser().getProfileImageUrl(),
                message.getAttachmentUrl(),
                message.getCreatedAt()
        );
    }

}
