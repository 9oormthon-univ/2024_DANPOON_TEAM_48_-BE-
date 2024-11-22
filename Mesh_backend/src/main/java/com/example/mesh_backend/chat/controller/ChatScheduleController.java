package com.example.mesh_backend.chat.controller;

import com.example.mesh_backend.chat.dto.request.ScheduleCreateRequest;
import com.example.mesh_backend.chat.dto.request.ScheduleUpdateRequest;
import com.example.mesh_backend.chat.dto.response.ScheduleListResponse;
import com.example.mesh_backend.chat.dto.response.ScheduleResponse;
import com.example.mesh_backend.chat.service.ChatScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/schedules")
public class ChatScheduleController {

    private final ChatScheduleService chatScheduleService;

    // 스케줄 생성
    @PostMapping
    @Operation(summary = "채팅방 스케줄 생성", description = "채팅방 스케줄 생성 API")
    public ResponseEntity<ScheduleResponse> createSchedule(@RequestBody ScheduleCreateRequest request) {
        ScheduleResponse schedule = chatScheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    // 스케줄 수정
    @PatchMapping("/{scheduleId}")
    @Operation(summary = "채팅방 스케줄 수정", description = "채팅방 스케줄 수정 API")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestBody ScheduleUpdateRequest request) {
        ScheduleResponse updatedSchedule = chatScheduleService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(updatedSchedule);
    }

    //특정 채팅방 스케줄 목록 확인
    @GetMapping("/{chatRoomId}")
    @Operation(summary = "채팅방 스케줄 목록", description = "채팅방 스케줄 목록 API")
    public ResponseEntity<ScheduleListResponse> getSchedulesByChatRoom(@PathVariable("chatRoomId") Long chatRoomId) {
        ScheduleListResponse scheduleListResponse = chatScheduleService.getSchedulesByChatRoom(chatRoomId);
        return ResponseEntity.ok(scheduleListResponse);
    }



}
