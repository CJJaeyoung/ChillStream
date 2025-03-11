package com.example.chillStream.dto;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemCrawlSearchDto {
   
   private String searchBy;
   private MainGenre mainGenre; // 메인 장르
   private SubGenre subGenre;
   private String searchQuery = "";
}
