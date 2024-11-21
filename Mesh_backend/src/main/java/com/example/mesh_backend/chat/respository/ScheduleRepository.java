package com.example.mesh_backend.chat.respository;

import com.example.mesh_backend.chat.entity.ChatRoom;
import com.example.mesh_backend.chat.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Schedule findTopByChatRoomAndDateAfterOrderByDateAsc(ChatRoom chatRoom, LocalDate currentDate);
}
