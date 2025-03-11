package com.example.chillStream.repository;

import com.example.chillStream.entity.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {
    Thumbnail findByItemId(Long ItemId);
}
