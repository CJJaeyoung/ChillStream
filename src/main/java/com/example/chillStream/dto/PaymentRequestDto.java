package com.example.chillStream.dto;

import com.example.chillStream.constant.Subscription;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentRequestDto {
   private String subscribeNm;   // 구독 이름
   private String impUid;        // Iamport 결제 고유 ID
   private String merchantUid;   // 주문 번호
   private int price;            // 결제된 금액
   private String status;        // 결제 상태
   private String buyerName;     // 사용자 이름
   private String buyerEmail;    // 사용자 이메일
   private String buyerTel;      // 사용자 전화번호
   private LocalDateTime paymentDate;
   private LocalDateTime subscriptionEndDate; // 구독 종료일
   private Subscription subscription;
   private Long videoId;
   private boolean refundable;
   private String CustomerUid;
   private int amount;
}
