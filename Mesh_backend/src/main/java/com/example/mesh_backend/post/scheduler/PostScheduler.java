package com.example.mesh_backend.post.scheduler;

import com.example.mesh_backend.post.entity.Post;
import com.example.mesh_backend.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostScheduler {

    private final PostRepository postRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void decrementDDay() {
        // D_day가 0보다 큰 모든 게시글 조회
        List<Post> postsWithDDay = postRepository.findAllBydDayGreaterThan(0);

        // D_day 감소
        postsWithDDay.forEach(post -> {
            post.setDDay(post.getDDay() - 1);
        });

        // 변경된 데이터 저장
        postRepository.saveAll(postsWithDDay);
    }
}
