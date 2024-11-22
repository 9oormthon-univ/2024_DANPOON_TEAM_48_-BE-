package com.example.mesh_backend.chat.respository;

import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.ChatRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatroomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT c FROM ChatRoom c " +
            "JOIN JoinChat jc1 ON c.id = jc1.chatRoom.id " +
            "JOIN JoinChat jc2 ON c.id = jc2.chatRoom.id " +
            "WHERE jc1.user.userId = :userId1 AND jc2.user.userId = :userId2 " +
            "AND c.postId = :postId")
    Optional<ChatRoom> findOneToOneChatRoomByPost(@Param("userId1") Long userId1,
                                                  @Param("userId2") Long userId2,
                                                  @Param("postId") Long postId);

    Optional<ChatRoom> findByPostIdAndChatRoomType(Long postId, ChatRoomType chatRoomType);
}