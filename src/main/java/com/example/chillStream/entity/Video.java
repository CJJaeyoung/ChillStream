package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Video extends BaseEntity{
    @Id
    @Column(name = "video_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    
    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Subtitle subtitle;
    
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoProgress> videoProgress;
    
    
    private String videoUrl; // 비디오 경로
    private String videoName; // 비디오 저장 이름
    private String oriVideoName; // 비디오 본래 이름
    
    private Integer episodeNumber; // 에피소드 넘버
    private String title; //에피소드 제목
    
    @Column(nullable = false)
    private int playCount = 0; // 재생 횟수
    
    public void updateVideo(String oriVideoName, String videoName, String videoUrl, String title, Integer episodeNumber){
        this.videoName = videoName;
        this.videoUrl = videoUrl;
        this.oriVideoName = oriVideoName;
        this.episodeNumber = episodeNumber;
        this.title = title;
    }
    
    public void updateTitleAndNumber(String title, Integer episodeNumber){
        this.title = title;
        this.episodeNumber = episodeNumber;
    }
    
    public void incrementPlayCount(){
        this.playCount++;
    }
}
