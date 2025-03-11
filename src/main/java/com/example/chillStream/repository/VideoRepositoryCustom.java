package com.example.chillStream.repository;

import com.example.chillStream.dto.VideoSearchDto;
import com.example.chillStream.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VideoRepositoryCustom {
    Page<Video> getAdminVideoPage(Long itemId, VideoSearchDto videoSearchDto, Pageable pageable);
}
