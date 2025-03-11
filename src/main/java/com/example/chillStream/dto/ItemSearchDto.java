package com.example.chillStream.dto;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {
    private String searchDateType; // 등록일 기준

    private MainGenre searchMainGenre; // 메인 장르

    private SubGenre searchSubGenre; // 서브 장르

    private String searchBy; // 조회 유형 ( 제목 / 등록자 )

    private String searchQuery = ""; //검색 단어
}
