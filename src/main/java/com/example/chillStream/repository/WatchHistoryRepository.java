package com.example.chillStream.repository;

import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.Video;
import com.example.chillStream.entity.WatchHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {
   Optional<WatchHistory> findByMemberAndVideo(Member member, Video video);
   Optional<WatchHistory> findByMemberIdAndVideoId(Long memberId, Long videoId);
   
   boolean existsByMemberIdAndPlayCountGreaterThan(Long memberId, int playCount);
   
   
   @Modifying
   @Transactional
   @Query("DELETE FROM WatchHistory wh WHERE wh.member.id = :memberId")
   void deleteByMemberId(@Param("memberId") Long memberId);
}