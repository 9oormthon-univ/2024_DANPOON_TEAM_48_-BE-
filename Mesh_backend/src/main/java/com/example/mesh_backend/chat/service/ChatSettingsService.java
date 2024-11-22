package com.example.mesh_backend.chat.service;

import com.example.mesh_backend.chat.dto.request.ChatSettingsUpdateRequest;
import com.example.mesh_backend.chat.dto.response.ChatSettingsResponse;
import com.example.mesh_backend.chat.dto.response.ParticipantResponse;
import com.example.mesh_backend.chat.dto.response.ScheduleListResponse;
import com.example.mesh_backend.chat.dto.response.ScheduleResponse;
import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.Schedule;
import com.example.mesh_backend.chat.respository.ChatroomRepository;
import com.example.mesh_backend.chat.respository.JoinChatRepository;
import com.example.mesh_backend.chat.respository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatSettingsService {

    private final ChatroomRepository chatroomRepository;
    private final JoinChatRepository joinChatRepository;
    private final ScheduleRepository scheduleRepository;


    // 채팅방 설정 수정
    @Transactional
    public ChatSettingsResponse updateChatSettings(Long chatRoomId, ChatSettingsUpdateRequest request) {
        ChatRoom chatRoom = chatroomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. : " + chatRoomId));

        if (request.getNotionLink() != null) {
            chatRoom.setNotionLink(request.getNotionLink());
        }
        if (request.getGithubLink() != null) {
            chatRoom.setGithubLink(request.getGithubLink());
        }
        if (request.getFigmaLink() != null) {
            chatRoom.setFigmaLink(request.getFigmaLink());
        }

        ChatRoom updatedChatRoom = chatroomRepository.save(chatRoom);

        List<ParticipantResponse> participants = joinChatRepository.findByChatRoomId(chatRoomId)
                .stream()
                .map(joinChat -> new ParticipantResponse(
                        joinChat.getUser().getUserId(),
                        joinChat.getUser().getNickname(),
                        joinChat.getUser().getProfileImageUrl()
                ))
                .collect(Collectors.toList());

        Schedule nextSchedule = scheduleRepository.findTopByChatRoomAndDateAfterOrderByDateAsc(chatRoom, LocalDate.now());
        ScheduleResponse scheduleResponse = null;
        if (nextSchedule != null) {
            scheduleResponse = new ScheduleResponse(
                    nextSchedule.getDate(),
                    nextSchedule.getTime(),
                    nextSchedule.getDescription()
            );
        }

        return new ChatSettingsResponse(
                chatRoom.getId(),
                chatRoom.getNotionLink(),
                chatRoom.getGithubLink(),
                chatRoom.getFigmaLink(),
                participants,
                scheduleResponse
        );
    }


    // 채팅방 설정 조회
    @Transactional(readOnly = true)
    public ChatSettingsResponse getChatSettings(Long chatRoomId) {
        ChatRoom chatRoom = chatroomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방을 찾을 수 없습니다. : " + chatRoomId));

        List<ParticipantResponse> participants = joinChatRepository.findByChatRoomId(chatRoomId)
                .stream()
                .map(joinChat -> new ParticipantResponse(
                        joinChat.getUser().getUserId(),
                        joinChat.getUser().getNickname(),
                        joinChat.getUser().getProfileImageUrl()
                ))
                .collect(Collectors.toList());

        Schedule nextSchedule = scheduleRepository.findTopByChatRoomAndDateAfterOrderByDateAsc(chatRoom, LocalDate.now());

        ScheduleResponse scheduleResponse = null;
        if (nextSchedule != null) {
            scheduleResponse = new ScheduleResponse(
                    nextSchedule.getDate(),
                    nextSchedule.getTime(),
                    nextSchedule.getDescription()
            );
        }

        return new ChatSettingsResponse(
                chatRoom.getId(),
                chatRoom.getNotionLink(),
                chatRoom.getGithubLink(),
                chatRoom.getFigmaLink(),
                participants,
                scheduleResponse
        );
    }



}
