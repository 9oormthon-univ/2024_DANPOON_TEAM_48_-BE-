package com.example.mesh_backend.chat.controller;

import com.example.mesh_backend.chat.dto.request.MessageRequest;
import com.example.mesh_backend.chat.dto.response.MessageResponse;
import com.example.mesh_backend.chat.entity.Message;
import com.example.mesh_backend.chat.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/messages")
@Tag(name = "채팅 메세지", description = "채팅 메세지관련 API")
public class MessageController {

    private final MessageService messageService;

    //메세지 송신
    @PostMapping("/send")
    @Operation(summary = "메세지 송신", description = "채팅방에 메세지 송신하는 API")
    public ResponseEntity<MessageResponse> sendMessage(
            @RequestPart(value ="messageRequest", required = false) MessageRequest messageRequest,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) {
        try {
            Message message = messageService.saveMessage(
                    messageRequest.getUserId(),
                    messageRequest.getChatRoomId(),
                    messageRequest.getContent(),
                    attachment
            );
            return ResponseEntity.ok(MessageResponse.fromEntity(message));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }

    //메세지 수신
    @GetMapping("/recieve")
    public ResponseEntity<List<MessageResponse>> receiveMessages(
            @RequestParam Long chatRoomId,
            @RequestParam Long lastMessageId) {
        List<MessageResponse> newMessages = messageService.getMessagesAfter(chatRoomId, lastMessageId);

        // 메시지가 없더라도 빈 리스트를 반환
        return ResponseEntity.ok(newMessages);
    }


    // 채팅방의 모든 메시지 가져오기
    @GetMapping("/list/{roomId}")
    @Operation(summary = "채팅방 메시지 조회", description = "해당 채팅방의 모든 메시지를 시간 순서대로 조회")
    public ResponseEntity<List<MessageResponse>> getChatRoomMessages(@PathVariable("roomId") Long roomId) {
        List<MessageResponse> messages = messageService.getChatRoomMessages(roomId);
        return ResponseEntity.ok(messages);
    }

}
