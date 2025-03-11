package com.example.chillStream.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdSearchDto {

    private String searchDateType; //등록일 기준

    private String searchBy; //조회 유형(등록자 / 제목)

    private String searchQuery = ""; //검색어
}
