package com.example.chillStream.service;

import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.Video;
import com.example.chillStream.entity.VideoProgress;
import com.example.chillStream.repository.MemberRepository;
import com.example.chillStream.repository.VideoProgressRepository;
import com.example.chillStream.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoProgressService {
    private final VideoProgressRepository videoProgressRepository; //사용자의 시청기록을 조회하는 레포지토리
    private final MemberRepository memberRepository;
    private final VideoRepository videoRepository;

    public void saveProgress(Long memberId, Long videoId, int lastPlayPosition){
        Member member = memberRepository.findById(memberId).orElseThrow(EntityNotFoundException::new);
        Video video = videoRepository.findById(videoId).orElseThrow(EntityNotFoundException::new);
        //시청 기록 조회 후 없으면 새로 생성
        VideoProgress progress = videoProgressRepository.findByMemberIdAndVideoId(member.getId(), video.getId())
                .orElse(new VideoProgress());

        progress.updateVideoProgress(member, video, lastPlayPosition);
        videoProgressRepository.save(progress);
    }

    public int getPosition(Long memberId, Long videoId){
        return videoProgressRepository.findByMemberIdAndVideoId(memberId, videoId) //멤버id와 videoId로 시청기록 조회
                .map(VideoProgress::getLastPlayedPosition) //해당하는 객체의 lastPlayedPosition을 호출후 리턴
                .orElse(0); //없으면 0을 리턴
    }

}
