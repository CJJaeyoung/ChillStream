package com.example.chillStream.entity;

import com.example.chillStream.constant.Subscription;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Payment {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "subscribe_nm", nullable = false)
   private String subscribeNm; // 구독 이름

   @Column(name = "imp_uid", unique = true, nullable = false)
   private String impUid; // Iamport 결제 고유 ID

   @Column(name = "merchant_uid", unique = true, nullable = false)
   private String merchantUid; // 주문 번호

   @Column(name = "price", nullable = false)
   private int price; // 결제된 금액

   @Column(name = "status", nullable = false)
   private String status; // 결제 상태

   @Column(name = "buyer_name", nullable = false)
   private String buyerName; // 사용자 이름

   @Column(name = "buyer_email", nullable = false)
   private String buyerEmail; // 사용자 이메일

   @Column(name = "buyer_tel", nullable = false)
   private String buyerTel; // 사용자 전화번호

   @Column(name = "payment_date", nullable = false)
   private LocalDateTime paymentDate = LocalDateTime.now(); // 결제 날짜

   @Enumerated(EnumType.STRING)
   private Subscription subscription; // 구독 타입
   
   @OneToOne
   @JoinColumn(name = "Membership_plan_id") // Membership_plan 연결
   private MembershipPlan MembershipPlan;
   
   private boolean refundable; // 결제별 환불 가능 여부

   private String customerUid;
   
   
   
   // 생성자
   public Payment(String subscribeNm, String impUid, String merchantUid, int price, String status,
                  String buyerName, String buyerEmail, String buyerTel, LocalDateTime paymentDate, Subscription subscription) {
      this.subscribeNm = subscribeNm;
      this.impUid = impUid;
      this.merchantUid = merchantUid;
      this.price = price;
      this.status = status;
      this.buyerName = buyerName;
      this.buyerEmail = buyerEmail;
      this.buyerTel = buyerTel;
      this.paymentDate = paymentDate;
      this.subscription = subscription; // 구독 상태를 파라미터로 받음
   }
}
