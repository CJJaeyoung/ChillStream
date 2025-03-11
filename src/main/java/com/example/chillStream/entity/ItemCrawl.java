package com.example.chillStream.entity;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_crawl")
@Getter
@Setter
@NoArgsConstructor
public class ItemCrawl {
    
    @Id
    @Column(name = "item_crawl_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String itemDetail;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MainGenre mainGenre;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SubGenre subGenre;
    
    private Integer runningTime;
    
    @Column(length = 1000)
    private String thumbnailUrl;
    
    private String releaseDate;
    
    @Column(unique = true, nullable = false)
    private Integer tmdbId;  // TMDB API의 고유 ID
    
    @Column
    private Double voteAverage;  // 평점
    
    @Column
    private Double popularity;   // 인기도
    
    @Column(columnDefinition = "TEXT")
    private String trailerUrl;
    
    @Column
    private String productionCountries; // 여러 국가를 콤마로 구분하여 저장
} 