package com.example.mesh_backend.chat.service;

import com.example.mesh_backend.chat.dto.response.MessageResponse;
import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.Message;
import com.example.mesh_backend.chat.respository.ChatroomRepository;
import com.example.mesh_backend.chat.respository.JoinChatRepository;
import com.example.mesh_backend.chat.respository.MessageRepository;
import com.example.mesh_backend.common.utils.S3Uploader;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatroomRepository chatroomRepository;
    private final JoinChatRepository joinChatRepository;
    private final S3Uploader s3Uploader;

    /**
     * 메시지 저장
     */
    @Transactional
    public Message saveMessage(Long userId, Long chatRoomId, String content, MultipartFile attachment) throws IOException {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        // 채팅방 조회
        ChatRoom chatRoom = chatroomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        // 첨부파일 업로드
        String attachmentUrl = null;
        if (attachment != null && !attachment.isEmpty()) {
            attachmentUrl = s3Uploader.uploadFiles(attachment, "chat");
        }

        // 메시지 생성
        Message message = Message.builder()
                .user(user)
                .chatRoom(chatRoom)
                .content(content)
                .attachmentUrl(attachmentUrl)
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }


    public Long getLatestMessageId(Long chatRoomId) {
        return messageRepository.findFirstByChatRoomIdOrderByIdDesc(chatRoomId)
                .map(Message::getId)
                .orElse(0L);
    }

    public boolean isUserInChatRoom(Long userId, Long chatRoomId) {
        return joinChatRepository.existsByUserIdAndChatRoomId(userId, chatRoomId);
    }

    /**
     * 특정 채팅방에서 새로운 메시지 조회
     */
    @Transactional(readOnly = true)
    public List<Message> getNewMessages(Long chatRoomId, Long lastMessageId) {
        if (lastMessageId == 0) {
            // lastMessageId가 0일 경우 가장 최신 메시지부터 시작
            lastMessageId = messageRepository.findMaxIdByChatRoomId(chatRoomId);
        }
        return messageRepository.findByChatRoomIdAndIdGreaterThanOrderByIdAsc(chatRoomId, lastMessageId);
    }

    public List<MessageResponse> getMessagesAfter(Long chatRoomId, Long lastMessageId) {
        // 메시지 조회
        List<Message> messages = messageRepository.findByChatRoomIdAndIdGreaterThan(chatRoomId, lastMessageId);

        // DTO로 변환하여 반환
        return messages.stream()
                .map(message -> new MessageResponse(
                        message.getUser().getUserId(),
                        message.getId(),
                        message.getContent(),
                        message.getUser().getNickname(),
                        message.getUser().getProfileImageUrl(),
                        message.getAttachmentUrl(),
                        message.getCreatedAt()))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<MessageResponse> getChatRoomMessages(Long roomId) {

        List<Message> messages = messageRepository.findAllByChatRoomIdOrderByCreatedAtAsc(roomId);

        return messages.stream()
                .map(MessageResponse::fromEntity)
                .collect(Collectors.toList());
    }

}
