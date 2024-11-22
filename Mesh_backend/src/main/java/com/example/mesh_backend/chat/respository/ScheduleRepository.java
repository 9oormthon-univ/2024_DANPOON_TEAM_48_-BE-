package com.example.mesh_backend.chat.respository;

import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule findTopByChatRoomAndDateAfterOrderByDateAsc(ChatRoom chatRoom, LocalDate currentDate);

    @Query("SELECT s FROM Schedule s WHERE s.chatRoom = :chatRoom AND (s.date < :currentDate OR (s.date = :currentDate AND s.time < :currentTime))")
    List<Schedule> findByChatRoomAndDateTimeBefore(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime
    );

    @Query("SELECT s FROM Schedule s WHERE s.chatRoom = :chatRoom AND (s.date > :currentDate OR (s.date = :currentDate AND s.time >= :currentTime)) ORDER BY s.date ASC, s.time ASC")
    List<Schedule> findByChatRoomAndDateTimeAfterOrderByDateTimeAsc(
            @Param("chatRoom") ChatRoom chatRoom,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime
    );


}
