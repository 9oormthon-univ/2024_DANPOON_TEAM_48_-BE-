package com.example.mesh_backend.chat.dto.response;

import com.example.mesh_backend.chat.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResponse {
    private Long scheduleId;
    private Long chatRoomId;
    private String description;
    private LocalDate date;
    private LocalTime time;

    public ScheduleResponse(LocalDate date, LocalTime time, String description) {
        this.date = date;
        this.time = time;
        this.description = description;
    }


    public static ScheduleResponse of(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getChatRoom().getId(),
                schedule.getDescription(),
                schedule.getDate(),
                schedule.getTime()
        );
    }
}