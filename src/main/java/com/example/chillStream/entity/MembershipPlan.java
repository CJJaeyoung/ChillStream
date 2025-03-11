package com.example.chillStream.entity;

import com.example.chillStream.constant.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Membership_plan")
@Getter
@Setter
@NoArgsConstructor
public class MembershipPlan {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "next_payment_date", nullable = false)
   private LocalDate nextPaymentDate; // 다음 결제일

   @Enumerated(EnumType.STRING) // Enum을 문자열로 저장
   @Column(name = "status", nullable = false)
   private SubscriptionStatus subscriptionStatus; // 구독 상태 (ACTIVE, CANCELLED, PAUSED 등)

   @Column(name = "failed_attempts")
   private int failedAttempts; // 결제 실패 횟수

   @OneToOne(mappedBy = "MembershipPlan", cascade = CascadeType.ALL)
   private Payment payment; // Payment와 1:1 관계
   
   @OneToOne
   @JoinColumn(name = "member_id")
   private Member member;
   
   public MembershipPlan(LocalDate nextPaymentDate, SubscriptionStatus subscriptionStatus) {
      this.nextPaymentDate = nextPaymentDate;
      this.subscriptionStatus = subscriptionStatus;
      this.failedAttempts = 0; // 초기화
   }
}