package com.example.chillStream.entity;

import com.example.chillStream.constant.Country;
import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private MainGenre mainGenre;

    @Enumerated(EnumType.STRING)
    private SubGenre subGenre;

    @Enumerated(EnumType.STRING)
    private Country country;

    private int viewCount;  // 조회수
    private double rating;  // 평점
} 