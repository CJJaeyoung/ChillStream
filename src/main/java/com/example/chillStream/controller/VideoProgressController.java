package com.example.chillStream.controller;

import com.example.chillStream.dto.VideoProgressDto;
import com.example.chillStream.service.VideoProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/video-progress")
@RequiredArgsConstructor
public class VideoProgressController {

    private final VideoProgressService videoProgressService;

    @PostMapping(value = "/save")
    public @ResponseBody ResponseEntity saveProgress(@RequestBody VideoProgressDto videoProgressDto){

        videoProgressService.saveProgress(videoProgressDto.getMemberId(), videoProgressDto.getVideoId(), videoProgressDto.getLastPlayedPosition());
        return ResponseEntity.ok("Progress saved");
    }

    @GetMapping(value = "/get/{memberId}/{videoId}")
    public @ResponseBody ResponseEntity getProgress(@PathVariable("memberId") Long memberId, @PathVariable("videoId") Long videoId){
        int lastPlayedPosition = videoProgressService.getPosition(memberId, videoId);
        return ResponseEntity.ok(lastPlayedPosition);
    }
}
