package com.example.mesh_backend.chat.service;

import com.example.mesh_backend.chat.dto.MemberDTO;
import com.example.mesh_backend.chat.dto.TeamMembersDTO;
import com.example.mesh_backend.chat.dto.response.ChatRoomDetailsResponse;
import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.ChatRoomType;
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

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("모집공고를 찾을 수 없습니다."));

        User requester = userRepository.findById(requesterId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        String chatRoomName = post.getPostTitle() + " 채팅방";

        ChatRoom chatRoom = new ChatRoom(chatRoomName, requester, postId, ChatRoomType.일대일);
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


    //팀 채팅방 생성 -> 닉네임 이용해서 채팅방 초대
    @Transactional
    public ChatRoom createTeamChatRoom(Post post, List<User> teamMembers) {
        String chatRoomName = post.getPostTitle() + " 팀 채팅방";

        // 기존 팀 채팅방 확인
        Optional<ChatRoom> existingChatRoom = chatroomRepository.findByPostIdAndChatRoomType(post.getPostId(), ChatRoomType.팀);
        if (existingChatRoom.isPresent()) {
            return existingChatRoom.get();
        }

        // 팀 채팅방 생성
        ChatRoom chatRoom = new ChatRoom(chatRoomName, post.getUser(), post.getPostId(), ChatRoomType.팀);
        chatroomRepository.save(chatRoom);

        // 팀 멤버 추가
        for (User user : teamMembers) {
            joinChatRepository.save(new JoinChat(user, chatRoom));
        }

        return chatRoom;
    }


    private void addTeamMembersToChatRoom(ChatRoom chatRoom, TeamMembersDTO teamMembersDTO) {
        addMembers(chatRoom, teamMembersDTO.getPmMembers());
        addMembers(chatRoom, teamMembersDTO.getBackMembers());
        addMembers(chatRoom, teamMembersDTO.getFrontMembers());
        addMembers(chatRoom, teamMembersDTO.getDesignMembers());
    }

    private void addMembers(ChatRoom chatRoom, List<MemberDTO> members) {
        for (MemberDTO member : members) {
            User user = userRepository.findByNickname(member.getNickname())
                    .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다: " + member.getNickname()));
            joinChatRepository.save(new JoinChat(user, chatRoom));
        }
    }


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
