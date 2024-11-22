package com.example.mesh_backend.chat.entity;
import com.example.mesh_backend.login.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="chat_room_id",unique = true)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator; // 채팅방을 생성한 유저

    @Column(name = "post_id", nullable = false)
    private Long postId;

    private String notionLink;

    private String githubLink;

    private String figmaLink;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    public ChatRoom(String roomName, User creator, Long postId, ChatRoomType chatRoomType) {
        this.roomName = roomName;
        this.creator = creator;
        this.postId = postId;
        this.chatRoomType = chatRoomType;
    }
}
