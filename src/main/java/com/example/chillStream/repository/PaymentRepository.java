package com.example.chillStream.repository;

import com.example.chillStream.constant.Subscription;
import com.example.chillStream.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long> {
   Page<Payment> findByBuyerEmail(String buyerEmail, Pageable pageable);
   
   // 특정 구독 상태로 조회
   Page<Payment> findBySubscription(Subscription subscription, Pageable pageable);
   
   // 구독 상태와 날짜로 검색
   List<Payment> findBySubscriptionAndPaymentDateBefore(Subscription subscription, LocalDateTime dateTime);
   
   // 내림차순 정렬 메서드
   default Page<Payment> findByBuyerEmailSorted(String buyerEmail, Pageable pageable) {
      // Pageable에서 페이지 정보 추출
      int page = pageable.getPageNumber();
      int size = pageable.getPageSize();
      
      // 정렬 추가된 Pageable 생성
      Pageable sortedByDateDesc = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
      
      return findByBuyerEmail(buyerEmail, sortedByDateDesc);
      
   }
   
   Optional<Payment> findByImpUid(String impUid); // Iamport 결제 ID로 데이터 조회
   
   
   List<Payment> findByStatus(String status);
   
   @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.paymentDate BETWEEN :startDate AND :endDate")
   List<Payment> findByStatusAndPaymentDateBetween(
         @Param("status") String status,
         @Param("startDate") LocalDateTime startDate,
         @Param("endDate") LocalDateTime endDate
   );
}