package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@ToString
public class VideoProgress extends BaseTimeEntity{
    @Id
    @Column(name = "video_progress_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    private int lastPlayedPosition; // 시청 중단 기록(초단위)

    public void updateVideoProgress(Member member, Video video, int lastPlayedPosition){
        this.member = member;
        this.video = video;
        this.lastPlayedPosition = lastPlayedPosition;
    }

}
