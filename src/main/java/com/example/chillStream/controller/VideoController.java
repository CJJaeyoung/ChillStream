package com.example.chillStream.controller;

import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.dto.*;
import com.example.chillStream.entity.Item;
import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.Video;
import com.example.chillStream.repository.ItemRepository;
import com.example.chillStream.service.MemberService;
import com.example.chillStream.service.VideoService;
import com.example.chillStream.service.WatchHistoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class VideoController {
    
    private final VideoService videoService;
    private final ItemRepository itemRepository;
    private final WatchHistoryService watchHistoryService;
    
    // 동영상 재생 페이지
    @GetMapping(value = "/video/{itemId}/{episodeNumber}")
    public String subscribeCheck(@PathVariable("episodeNumber") Integer episodeNumber,
                                 @PathVariable("itemId") Long itemId,
                                 Model model) {
        String email = SecurityUtil.getCurrentUserEmail();
        try {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+episodeNumber);
            Video video = videoService.playVideo(itemId, episodeNumber);
            
            watchHistoryService.updateUserPlayCount(email, video);
            
            MemberDto memberDto = videoService.getMemberDto(email);
            AdDto adDto = videoService.getAd();
            SubtitleDto subtitleDto = videoService.getSubtitle(video.getId());
            
            model.addAttribute("subtitleDto", subtitleDto);
            model.addAttribute("video", video);
            model.addAttribute("memberDto", memberDto);
            model.addAttribute("adDto", adDto);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/item/{itemId}";
        }
        return "video/play";
    }
    
    // 관리자 영상 관리 페이지(게시판)
    @GetMapping(value = { "/admin/{itemId}/videos", "/admin/{itemId}/videos/{page}" })
    public String videoMng(@PathVariable("page") Optional<Integer> page,
                           @PathVariable("itemId")Long itemId,
                           VideoSearchDto videoSearchDto, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        
        Page<Video> videos = videoService.getAdminVideoPage(itemId, videoSearchDto, pageable);
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("videos", videos);
        model.addAttribute("videoSearchDto", videoSearchDto);
        model.addAttribute("item",item);
        model.addAttribute("maxPage", 10);
        return "video/videoMng";
    }
    
    // 영상 등록 Get
    @GetMapping(value = "/admin/{itemId}/video/new")
    public String videoForm(@PathVariable("itemId") Long itemId, Model model) {
        model.addAttribute("videoFormDto", new VideoFormDto(itemId));
        return "video/videoForm";
    }
    
    // 영상 등록 Post
    @PostMapping(value = "/admin/{itemId}/video/new")
    public String videoNew(@Valid VideoFormDto videoFormDto,
                           BindingResult bindingResult,
                           @PathVariable("itemId") Long itemId,
                           @RequestParam("videoFile") MultipartFile videoFile,
                           @RequestParam("subtitleFile") MultipartFile subtitleFile,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "video/videoForm";
        }
        if (videoFile.isEmpty()) {
            model.addAttribute("errorMessage", "영상 파일은 필수 입력값입니다.");
            return "video/videoForm";
        }
        try {
            videoService.saveVideo(itemId, videoFormDto, videoFile, subtitleFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "영상 등록 중 에러가 발생하였습니다.");
            return "video/videoForm";
        }
        
        return "redirect:/admin/{itemId}/videos";
    }
    
    //영상 수정(get)
    @GetMapping(value = "/admin/{itemId}/video/{videoId}")
    public String videoDtl(@PathVariable("itemId") Long itemId,
                           @PathVariable("videoId") Long videoId,
                           Model model) {
        try {
            VideoFormDto videoFormDto = videoService.getVideoDtl(videoId);
            model.addAttribute("videoFormDto", videoFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 영상입니다.");
            model.addAttribute("videoFormDto", new VideoFormDto(itemId));
            return "video/videoForm";
        }
        return "video/videoForm";
    }
    
    //영상 수정(post)
    @PostMapping(value = "/admin/{itemId}/video/{videoId}")
    public String videoUpdate(@Valid VideoFormDto videoFormDto,
                              @PathVariable("itemId")Long itemId,
                              BindingResult bindingResult,
                              @RequestParam("videoFile") MultipartFile videoFile,
                              @RequestParam("subtitleFile") MultipartFile subtitleFile,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "video/videoForm";
        }
        try {
            videoService.updateVideo(itemId, videoFormDto, videoFile, subtitleFile);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "영상 수정 중 에러가 발생하였습니다.");
            return "video/videoForm";
        }
        
        return "redirect:/admin/{itemId}/videos";
    }
    
    //영상 삭제
    @DeleteMapping("/admin/{itemId}/video/{videoId}")
    public ResponseEntity<String> deleteVideo(@PathVariable("videoId") Long videoId) {
        try {
            videoService.deleteVideo(videoId);
            return new ResponseEntity<>("삭제 성공", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("삭제 실패", HttpStatus.BAD_REQUEST);
        }
    }
}
