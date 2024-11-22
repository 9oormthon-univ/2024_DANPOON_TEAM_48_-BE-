package com.example.mesh_backend.apply.service;

import com.example.mesh_backend.apply.dto.ApplyRequestDTO;
import com.example.mesh_backend.apply.entity.Apply;
import com.example.mesh_backend.apply.repository.ApplyRepository;
import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.respository.MessageRepository;
import com.example.mesh_backend.chat.service.ChatService;
import com.example.mesh_backend.common.CustomException;
import com.example.mesh_backend.common.exception.ErrorCode;
import com.example.mesh_backend.login.entity.User;
import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import com.example.mesh_backend.chat.entity.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ApplyServiceImpl implements ApplyService {

    private final ApplyRepository applyRepository;
    private final PostRepository postRepository;
    private final ChatService chatService;

    private final MessageRepository messageRepository;
    public ApplyServiceImpl(ApplyRepository applyRepository, PostRepository postRepository,
                            ChatService chatService, MessageRepository messageRepository){
        this.applyRepository = applyRepository;
        this.postRepository = postRepository;
        this.chatService = chatService;
        this.messageRepository = messageRepository;
    }

    @Override
    public String applyToProject(User user, Long projectId, ApplyRequestDTO applyRequestDTO) {
        // Post 객체 조회
        Post post = postRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // Apply 객체 생성 및 저장
        Apply apply = new Apply();
        apply.setUser(user);
        apply.setPost(post);
        apply.setPart(applyRequestDTO.getPart());
        apply.setContents(applyRequestDTO.getContents());

        apply.setLeaderId(post.getUser().getUserId());

        applyRepository.save(apply);


        //일대일 채팅방 생성
        ChatRoom chatRoom = chatService.createOrGetOneToOneChatRoom(
                user.getUserId(), post.getUser().getUserId(), projectId
        );

        String userProfileUrl = "/api/v1/user/" + user.getUserId() + "/profile";

        // 자동 메시지 생성 및 전송
        String autoMessage = "[" + applyRequestDTO.getPart() + "]\n"
                + user.getNickname() + "님이 "
                + "\"" + post.getPostTitle() + "\" 모집 공고에 지원했습니다.\n"
                + "(전달 메시지: " + applyRequestDTO.getContents() + ")\n"
                + "지원자 프로필 보기: " + userProfileUrl;

        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setUser(user);
        message.setContent(autoMessage);
        messageRepository.save(message);


        return "지원이 성공적으로 완료되었습니다.";
    }
}
