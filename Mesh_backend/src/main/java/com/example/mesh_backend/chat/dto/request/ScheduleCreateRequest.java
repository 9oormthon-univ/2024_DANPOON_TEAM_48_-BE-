package com.example.mesh_backend.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateRequest {
    private Long chatRoomId;
    private LocalDate date;
    private LocalTime time;
    private String description;
}