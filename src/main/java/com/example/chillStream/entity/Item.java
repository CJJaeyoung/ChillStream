package com.example.chillStream.entity;

import com.example.chillStream.constant.Country;
import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import com.example.chillStream.dto.ItemFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "item")
public class Item extends BaseEntity {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title; // 제목
    
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String itemDetail; // 줄거리
    
    @Column(nullable = false)
    private String releaseDate; // 개봉일
    
    @Column(nullable = false)
    private Integer runningTime; // 러닝타임
    
    @Enumerated(EnumType.STRING)
    private Country country; // 제작 국가 (KOREA, USA, EUROPE, JAPAN, CHINA)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MainGenre mainGenre; // 메인 장르 (DRAMA, MOVIE, ANIMATION, ENTERTAINMENT)
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SubGenre subGenre; // 하위 장르 (FANTASY, SF, DRAMA, ROMANCE, COMEDY, ACTION, THRILLER, ANIMATION,
    // HISTORY)
    
    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Thumbnail thumbnail;
    
    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private Preview preview;
    
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();
    
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    
    public void updateItem(ItemFormDto itemFormDto) {
        this.title = itemFormDto.getTitle();
        this.itemDetail = itemFormDto.getItemDetail();
        this.releaseDate = itemFormDto.getReleaseDate();
        this.runningTime = itemFormDto.getRunningTime();
        this.country = itemFormDto.getCountry();
        this.mainGenre = itemFormDto.getMainGenre();
        this.subGenre = itemFormDto.getSubGenre();
    }
    
}
