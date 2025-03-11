package com.example.chillStream.service;

import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.Video;
import com.example.chillStream.entity.WatchHistory;
import com.example.chillStream.repository.MemberRepository;
import com.example.chillStream.repository.WatchHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WatchHistoryService {
   
   
   private final WatchHistoryRepository watchHistoryRepository;
   private final MemberRepository memberRepository;
   
   
   public Member getMember(String email) {
      return memberRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + email));
   }
   
   
   @Transactional
   public void incrementUserPlayCount(Member member, Video video) {
      WatchHistory watchHistory = watchHistoryRepository.findByMemberAndVideo(member, video)
            .orElseGet(() -> {
               WatchHistory newHistory = new WatchHistory();
               newHistory.setMember(member);
               newHistory.setVideo(video);
               newHistory.setPlayCount(0);
               newHistory.setLastWatched(LocalDateTime.now());
               System.out.println("새로운 WatchHistory 생성: " + newHistory);
               return newHistory;
            });
      
      watchHistory.incrementUserPlayCount();
      System.out.println("WatchHistory 업데이트 후: " + watchHistory);
      
      watchHistoryRepository.save(watchHistory);
   }
   
   public void updateUserPlayCount(String email, Video video) {
      Member member = getMember(email); // 사용자 조회
     incrementUserPlayCount(member, video); // 시청 기록 증가
   }
   
   
   // 시청 기록 확인
   public boolean hasViewHistory(Member member, Video video) {
      return watchHistoryRepository.findByMemberAndVideo(member, video)
            .map(history -> history.getPlayCount() > 0) // 시청 횟수가 0보다 큰지 확인
            .orElse(false); // 기록이 없으면 false 반환
   }
   
   public int getPlayCount(Member member, Long videoId) {
      return watchHistoryRepository.findByMemberIdAndVideoId(member.getId(), videoId)
            .map(WatchHistory::getPlayCount) // playCount 값을 가져옴
            .orElse(0); // 기록이 없으면 0 반환
   }
   
   public boolean hasViewHistory(Member member) {
      return watchHistoryRepository.existsByMemberIdAndPlayCountGreaterThan(member.getId(), 0);
   }
   
   @Transactional
   public void deleteWatchHistoryByMemberId(Long memberId) {
      watchHistoryRepository.deleteByMemberId(memberId);
   }
}