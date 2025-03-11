package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "watch_history")
public class WatchHistory {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "member_id", nullable = false)
   private Member member;
   
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "video_id", nullable = false)
   private Video video;
   
   @Column(nullable = false)
   private int playCount = 0;
   
   @Column(nullable = false)
   private LocalDateTime lastWatched;
   
   public void incrementUserPlayCount() {
      this.playCount++;
      this.lastWatched = LocalDateTime.now();
   }
}
