package com.example.chillStream.repository;

import com.example.chillStream.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {
    Optional<Video> findByItemId(Long itemId);
    
    Video findByItemIdAndEpisodeNumber(Long itemId, Integer episodeNumber);
    
    List<Video> findAllByItemId(Long itemId);
}