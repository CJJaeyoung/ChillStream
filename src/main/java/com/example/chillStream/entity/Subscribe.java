package com.example.chillStream.entity;

import com.example.chillStream.constant.Subscription;
import com.example.chillStream.dto.SubscribeFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "Subscribe")
@Getter
@Setter
@ToString
public class Subscribe extends BaseEntity {
   @Id
   @Column(name = "subscribe_id")
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id; // 상품코드
   
   @Column(nullable = false, length = 50)
   private String subscribeNm; // 상품명
   
   @Column(name = "price", nullable = false)
   private Integer price; // 가격
   
   @Lob
   @Column(nullable = false)
   private String subscribeDetail; // 상품상세설명

   @Column(name = "subscription")
   private Subscription subscription = Subscription.INACTIVE;
   
   @Column(name = "active", nullable = false)
   private boolean active = false; // 기본값: 비활성화(false)

   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(
           name="member_subscribe",
           joinColumns = @JoinColumn(name = "member_id"),
           inverseJoinColumns = @JoinColumn(name = "subscribe_id")
   )

   private List<Member> member;


   // ** 구독 활성화 상태 업데이트 메서드 **
   public void activate() {
      this.active = true;
   }
   
   public void deactivate() {
      this.active = false;
   }
   
   public void updateSubscribe(SubscribeFormDto subscribeFormDto) {
      this.subscribeNm = subscribeFormDto.getSubscribeNm();
      this.price = subscribeFormDto.getPrice();
      this.subscribeDetail = subscribeFormDto.getSubscribeDetail();
   }
}