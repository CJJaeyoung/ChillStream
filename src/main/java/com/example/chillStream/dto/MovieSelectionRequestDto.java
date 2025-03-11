package com.example.chillStream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieSelectionRequestDto {

    private Long id;      // 영화 ID
    private String mainGenre;  // 메인 장르
    private String subGenre;   // 서브 장르
    private Integer priority;  // 선택 순서
}