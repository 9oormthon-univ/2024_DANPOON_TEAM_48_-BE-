package com.example.mesh_backend.chat.service;

import com.example.mesh_backend.chat.dto.request.ScheduleCreateRequest;
import com.example.mesh_backend.chat.dto.request.ScheduleUpdateRequest;
import com.example.mesh_backend.chat.dto.response.ScheduleListResponse;
import com.example.mesh_backend.chat.dto.response.ScheduleResponse;
import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.Schedule;
import com.example.mesh_backend.chat.respository.ChatroomRepository;
import com.example.mesh_backend.chat.respository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ChatroomRepository chatroomRepository;

    // 스케줄 생성
    @Transactional
    public ScheduleResponse createSchedule(ScheduleCreateRequest request) {
        ChatRoom chatRoom = chatroomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다." + request.getChatRoomId()));

        Schedule schedule = new Schedule();
        schedule.setChatRoom(chatRoom);
        schedule.setDate(request.getDate());
        schedule.setTime(request.getTime());
        schedule.setDescription(request.getDescription());

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return new ScheduleResponse(
                savedSchedule.getDate(),
                savedSchedule.getTime(),
                savedSchedule.getDescription()
        );
    }

    // 스케줄 수정
    @Transactional
    public ScheduleResponse updateSchedule(Long scheduleId, ScheduleUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("스케줄을 찾을 수 없습니다. : " + scheduleId));

        if (request.getDate() != null) {
            schedule.setDate(request.getDate());
        }
        if (request.getTime() != null) {
            schedule.setTime(request.getTime());
        }
        if (request.getDescription() != null) {
            schedule.setDescription(request.getDescription());
        }

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return new ScheduleResponse(
                updatedSchedule.getDate(),
                updatedSchedule.getTime(),
                updatedSchedule.getDescription()
        );
    }


    //채팅방 스케줄 목록 조회
    @Transactional(readOnly = true)
    public ScheduleListResponse getSchedulesByChatRoom(Long chatRoomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatroomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다. : " + chatRoomId));

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // 지난 스케줄 삭제
        List<Schedule> pastSchedules = scheduleRepository.findByChatRoomAndDateTimeBefore(chatRoom, currentDate, currentTime);
        scheduleRepository.deleteAll(pastSchedules);

        // 남아있는 스케줄 정렬
        List<Schedule> remainingSchedules = scheduleRepository.findByChatRoomAndDateTimeAfterOrderByDateTimeAsc(chatRoom, currentDate, currentTime);

        return ScheduleListResponse.of(chatRoomId, remainingSchedules);
    }
}