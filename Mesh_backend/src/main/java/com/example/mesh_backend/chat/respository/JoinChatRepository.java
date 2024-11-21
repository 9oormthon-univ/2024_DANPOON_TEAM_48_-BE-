package com.example.mesh_backend.chat.respository;

import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.JoinChat;
import com.example.mesh_backend.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoinChatRepository extends JpaRepository<JoinChat, Long> {



    @Query("SELECT COUNT(jc) > 0 FROM JoinChat jc WHERE jc.user.userId = :userId AND jc.chatRoom.id = :chatRoomId")
    boolean existsByUserIdAndChatRoomId(@Param("userId") Long userId, @Param("chatRoomId") Long chatRoomId);

    List<JoinChat> findByUserUserId(Long userId);
    long countByChatRoom(ChatRoom chatRoom);

    List<JoinChat> findByChatRoomId(Long chatRoomId);
}
