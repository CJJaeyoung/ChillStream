package com.example.chillStream.repository;

import com.example.chillStream.entity.Subtitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubtitleRepository extends JpaRepository<Subtitle, Long> {
    Optional<Subtitle> findByVideoId(Long videoId);
    
    
}
