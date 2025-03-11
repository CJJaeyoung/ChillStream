package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
public class Subtitle extends BaseEntity{
    @Id
    @Column(name = "subtitle_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    private String oriSubtitleName;
    private String subtitleName;
    private String subtitleUrl;

    public void updateSubtitle(String oriSubtitleName, String subtitleName, String subtitleUrl){
        this.oriSubtitleName = oriSubtitleName;
        this.subtitleName = subtitleName;
        this.subtitleUrl = subtitleUrl;
    }
}
