package com.example.mesh_backend.chat.controller;

import com.example.mesh_backend.chat.dto.response.ChatRoomDetailsResponse;
import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@Tag(name = "채팅방", description = "채팅방관련 API")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/create")
    @Operation(summary = "채팅방 생성", description = "채팅방 생성 API")
    public ResponseEntity<?> createChatroom(@RequestParam Long requesterId, @RequestParam Long postId) {

        Long ownerId = chatService.getPostOwner(postId); //모집공고 작성자(ownerId) 조회
        ChatRoom chatRoom = chatService.createOrGetOneToOneChatRoom(requesterId, ownerId, postId);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/list")
    @Operation(summary = "참여한 채팅방 리스트", description = "로그인한 유저가 참여한 채팅방 리스트를 조회")
    public ResponseEntity<List<ChatRoomDetailsResponse>> getUserChatRooms(@RequestParam Long userId) {
        List<ChatRoomDetailsResponse> chatRooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }


}
