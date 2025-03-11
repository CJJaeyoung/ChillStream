package com.example.chillStream.dto;

import com.example.chillStream.constant.Subscription;
import com.example.chillStream.entity.Subscribe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;



@Getter
@Setter
public class SubscribeFormDto {
   private Long id;
   
   @NotBlank(message = "상품명은 필수 입력 값입니다.")
   private String subscribeNm;
   
   @NotNull(message = "가격은 필수 입력 값입니다.")
   private Integer price;
   
   @NotBlank(message = "상세 내용은 필수 입력 값입니다.")
   private  String subscribeDetail;

   private Subscription subscription; // 구독 상태 추가


   private static ModelMapper modelMapper = new ModelMapper();
   
   public Subscribe createSubscribe(){
      // SubscribeFormDto -> Subscribe 연결
      return modelMapper.map(this, Subscribe.class);
   }
   public static SubscribeFormDto of(Subscribe subscribe){
      // Subscribe -> SubscribeFormDto 연결
      return modelMapper.map(subscribe, SubscribeFormDto.class);
   }
}