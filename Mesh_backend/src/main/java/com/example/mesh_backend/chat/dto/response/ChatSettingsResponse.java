package com.example.mesh_backend.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatSettingsResponse {
    private Long chatRoomId;
    private String notionLink;
    private String githubLink;
    private String figmaLink;
    private List<ParticipantResponse> participants;
    private ScheduleResponse nextSchedule;
}