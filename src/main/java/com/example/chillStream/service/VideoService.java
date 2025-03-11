package com.example.chillStream.service;

import com.example.chillStream.dto.*;
import com.example.chillStream.entity.*;
import com.example.chillStream.exception.NoAdException;
import com.example.chillStream.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {
    
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;
    private final AdRepository adRepository;
    private final SubtitleRepository subtitleRepository;
    private final ItemRepository itemRepository;
    private final ContentsService contentsService;
    
    public Video playVideo(Long itemId, Integer episodeNumber) {
        Video video;
        if (episodeNumber == 0) {   // 회차가 1개일 때
            video = videoRepository.findByItemId(itemId).orElseThrow(EntityNotFoundException::new);
        } else {    // 회차가 복수일때
            video = videoRepository.findByItemIdAndEpisodeNumber(itemId, episodeNumber);
        }
        video.incrementPlayCount();     // 재생횟수 1 증가
        return video;
    }
    
    @Transactional(readOnly = true)
    public MemberDto getMemberDto(String email) throws ChangeSetPersister.NotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);
        return MemberDto.of(member);
    }
    
    public AdDto getAd() {
        List<Ad> adList = adRepository.findAll(); // 모든 광고 조회
        if(adList.isEmpty()){
            throw new NoAdException("등록된 광고가 없습니다. 광고를 등록해주세요.");
        }
        Random r = new Random(); // 랜덤 객체
        int randomIndex = r.nextInt(adList.size()); // 랜덤 index 생성
        
        Ad ad = adList.get(randomIndex); // 무작위 ad 뽑아내기
        return AdDto.of(ad); // 해당 ad를 dto로 바꾼 후 리턴
    }
    
    // 자막 가져오기
    @Transactional(readOnly = true)
    public SubtitleDto getSubtitle(Long videoId) throws ChangeSetPersister.NotFoundException {
        Subtitle subtitle = subtitleRepository.findByVideoId(videoId)
              .orElseThrow(ChangeSetPersister.NotFoundException::new);
        
        return SubtitleDto.of(subtitle);
    }
    
    // 영상 저장하기
    public void saveVideo(Long itemId, VideoFormDto videoFormDto, MultipartFile videoFile, MultipartFile subtitleFile)
          throws Exception {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        
        Video video = new Video();
        video.setItem(item);
        contentsService.saveVideoFile(videoFormDto, video, videoFile);
        
        Subtitle subtitle = new Subtitle();
        subtitle.setVideo(video);
        contentsService.saveSubtitleFile(subtitle, subtitleFile);
    }
    
    // 영상 업데이트
    public void updateVideo(Long itemId, VideoFormDto videoFormDto, MultipartFile videoFile, MultipartFile subtitleFile)
          throws Exception {
        
        Video video = videoRepository.findById(videoFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        Subtitle subtitle = subtitleRepository.findByVideoId(video.getId()).orElseThrow(EntityNotFoundException::new);
        
        contentsService.updateVideo(videoFormDto, videoFile);
        contentsService.updateSubtitle(subtitle.getId(), subtitleFile);
    }
    
    // 영상 삭제(자막 포함)
    public void deleteVideo(Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(EntityNotFoundException::new);
        videoRepository.delete(video);
    }
    
    // 영상 정보 가져오기 (수정)
    public VideoFormDto getVideoDtl(Long videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(EntityNotFoundException::new);
        VideoDto videoDto = VideoDto.of(video);
        
        Subtitle subtitle = subtitleRepository.findByVideoId(videoId).orElseThrow(EntityNotFoundException::new);
        SubtitleDto subtitleDto = SubtitleDto.of(subtitle);
        
        VideoFormDto videoFormDto = VideoFormDto.of(video);
        videoFormDto.setVideoDto(videoDto);
        videoFormDto.setSubtitleDto(subtitleDto);
        
        return videoFormDto;
    }
    
    // 영상 페이지 가져오기
    @Transactional(readOnly = true)
    public Page<Video> getAdminVideoPage(Long itemId, VideoSearchDto videoSearchDto, Pageable pageable) {
        return videoRepository.getAdminVideoPage(itemId, videoSearchDto, pageable);
    }
    
    // 비디오 Dto List 가져오기
    @Transactional(readOnly = true)
    public List<VideoFormDto> getVideoFormList(Long itemId) {
        // 단일 결과 대신 리스트로 조회
        List<Video> videos = videoRepository.findAllByItemId(itemId);
        
        return videos.stream()
              .map(video -> {
                  VideoFormDto videoFormDto = VideoFormDto.of(video);
                  return videoFormDto;
              })
              .collect(Collectors.toList());
    }
}
