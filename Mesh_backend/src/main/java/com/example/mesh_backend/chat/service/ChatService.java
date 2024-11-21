package com.example.mesh_backend.chat.service;

import com.example.mesh_backend.chat.dto.response.ChatRoomDetailsResponse;
import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.JoinChat;
import com.example.mesh_backend.chat.entity.Message;
import com.example.mesh_backend.chat.respository.ChatroomRepository;
import com.example.mesh_backend.chat.respository.JoinChatRepository;
import com.example.mesh_backend.chat.respository.MessageRepository;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.login.repository.UserRepository;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final JoinChatRepository joinChatRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageRepository messageRepository;
    
    
    //일대일 채팅방 생성
    @Transactional
    public ChatRoom createOrGetOneToOneChatRoom(Long requesterId, Long ownerId, Long postId) {

        // 기존 1:1 채팅방이 있는지 확인
        Optional<ChatRoom> existingChatRoom = chatroomRepository.findOneToOneChatRoomByPost(requesterId, ownerId, postId);
        if (existingChatRoom.isPresent()) {
            return existingChatRoom.get();
        }

        User requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        ChatRoom chatRoom = new ChatRoom("1:1 채팅방", requester, postId);
        chatroomRepository.save(chatRoom);

        joinChatRepository.save(new JoinChat(requester, chatRoom));
        joinChatRepository.save(new JoinChat(owner, chatRoom));

        return chatRoom;
    }

    public Long getPostOwner(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("모집공고를 찾을 수 없습니다."))
                .getUser()
                .getUserId();
    }


    //팀 채팅방 생성
//    @Transactional
//    public void handleStatusChange(Long postId) {
//        // 모집공고 조회
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new RuntimeException("모집공고 게시글을 찾을 수 없습니다."));
//
//        // 상태가 "모집완료"인지 확인
//        if ("모집완료".equals(post.getStatus())) {
//            User requester = post.getUser();
//
//            ChatRoom chatRoom = new ChatRoom("팀 채팅방", requester, postId);
//            chatroomRepository.save(chatRoom);
//
//            // 팀원 모두 초대 (작성자 포함)
//            List<Long> teamMemberIds = new ArrayList<>();
//
//            // 작성자 추가
//            teamMemberIds.add(requester.getUserId());
//
//            // PM_Match, Back_Match, Front_Match, Design_Match에서 유저 ID 추가
//            teamMemberIds.addAll(pmMatchRepository.findUserIdsByPostId(postId));
//            teamMemberIds.addAll(backMatchRepository.findUserIdsByPostId(postId));
//            teamMemberIds.addAll(frontMatchRepository.findUserIdsByPostId(postId));
//            teamMemberIds.addAll(designMatchRepository.findUserIdsByPostId(postId));
//
//            // JoinChat에 팀원 추가
//            teamMemberIds.forEach(memberId -> {
//                User user = userRepository.findById(memberId)
//                        .orElseThrow(() -> new RuntimeException("User not found"));
//                joinChatRepository.save(new JoinChat(user, chatRoom));
//            });
//        }
//    }


    //유저가 참여한 채팅방 리스트
    public List<ChatRoomDetailsResponse> getUserChatRooms(Long userId) {

        List<JoinChat> joinChats = joinChatRepository.findByUserUserId(userId);

        return joinChats.stream().map(joinChat -> {
            ChatRoom chatRoom = joinChat.getChatRoom();
            Message lastMessage = messageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom);
            long participantCount = joinChatRepository.countByChatRoom(chatRoom);
            User lastMessageSender = lastMessage != null ? lastMessage.getUser() : null;

            return new ChatRoomDetailsResponse(
                    chatRoom.getId(),
                    chatRoom.getRoomName(),
                    lastMessage != null ? lastMessage.getContent() : null,
                    lastMessageSender != null ? lastMessageSender.getNickname() : null,
                    lastMessageSender != null ? lastMessageSender.getProfileImageUrl() : null,
                    lastMessage != null ? lastMessage.getCreatedAt() : null,
                    participantCount
            );
        }).collect(Collectors.toList());
    }



}
