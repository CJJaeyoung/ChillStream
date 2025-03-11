package com.example.chillStream.repository;

import com.example.chillStream.entity.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {
    Optional<VideoProgress> findByMemberIdAndVideoId(Long memberId, Long videoId);
}
