package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Thumbnail {
    @Id
    @Column(name = "thumbnail_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private String thumbnailUrl; // 썸네일 경로
    private String thumbnailName; // 썸네일 저장 이름
    private String oriThumbnailName; // 썸네일 본래 이름

    public void updateThumbnail(String oriThumbnailName, String thumbnailName, String thumbnailUrl){
        this.thumbnailName = thumbnailName;
        this.thumbnailUrl = thumbnailUrl;
        this.oriThumbnailName = oriThumbnailName;
    }
}
