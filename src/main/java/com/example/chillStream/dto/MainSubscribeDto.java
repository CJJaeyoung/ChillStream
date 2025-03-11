package com.example.chillStream.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainSubscribeDto {
   private Long id;
   private String subscribeNm;
   private String subscribeDetail;
   private Integer price;

   @QueryProjection //Querydsl 결과 조회 시 MainSubcribeDto 객체로 바로 오도록  활용
   public MainSubscribeDto(Long id, String subscribeNm, String subscribeDetail, Integer price){
      this.id = id;
      this.subscribeNm = subscribeNm;
      this.subscribeDetail = subscribeDetail;
      this.price = price;
   }
}