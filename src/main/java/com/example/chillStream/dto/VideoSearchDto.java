package com.example.chillStream.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoSearchDto {
    private String searchDateType; // 등록일 기준

    private String searchBy; // 조회 유형 ( 회차 / 제목 / 등록자 )

    private Long itemId; // 콘텐츠 id

    private String searchQuery = ""; //검색 단어
}
