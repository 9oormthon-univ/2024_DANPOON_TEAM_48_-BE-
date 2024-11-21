package com.example.mesh_backend.chat.respository;

import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<Message> findFirstByChatRoomIdOrderByIdDesc(Long chatRoomId);
    @Query("SELECT MAX(m.id) FROM Message m WHERE m.chatRoom.id = :chatRoomId")
    Long findMaxIdByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    @Query("SELECT m FROM Message m JOIN FETCH m.user WHERE m.chatRoom.id = :chatRoomId AND m.id > :lastMessageId ORDER BY m.id ASC")
    List<Message> findByChatRoomIdAndIdGreaterThanOrderByIdAsc(@Param("chatRoomId") Long chatRoomId, @Param("lastMessageId") Long lastMessageId);

    List<Message> findByChatRoomIdAndIdGreaterThan(Long chatRoomId, Long lastMessageId);

    Message findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    List<Message> findAllByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);


}