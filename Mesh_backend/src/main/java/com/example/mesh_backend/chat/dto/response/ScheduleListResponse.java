package com.example.mesh_backend.chat.dto.response;

import com.example.mesh_backend.chat.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ScheduleListResponse {
    private Long chatRoomId;
    private List<ScheduleResponse> schedules;

    public static ScheduleListResponse of(Long chatRoomId, List<Schedule> scheduleList) {
        List<ScheduleResponse> scheduleResponses = scheduleList.stream()
                .map(ScheduleResponse::of)
                .toList();

        return new ScheduleListResponse(chatRoomId, scheduleResponses);
    }
}