package com.example.chillStream.controller;

import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.constant.SubscriptionStatus;
import com.example.chillStream.dto.PaymentRequestDto;
import com.example.chillStream.entity.*;
import com.example.chillStream.repository.MemberRepository;
import com.example.chillStream.repository.ScheduledPaymentRepository;
import com.example.chillStream.service.PaymentService;
import com.example.chillStream.service.RefundService;
import com.example.chillStream.service.WatchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.time.Duration;
import java.util.ArrayList;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PaymentController {
   
   private final PaymentService paymentService;
   private final MemberRepository memberRepository;
   private final RefundService refundService;
   private final WatchHistoryService watchHistoryService;
   private final ScheduledPaymentRepository scheduledPaymentRepository;
   
   
   @GetMapping(value = "/payment/subscription")
   public String paymentSubscription() {
      return "payment/subscription";
   }
   
   @PostMapping("/payment/complete")
   @ResponseBody
   public String completePayment(@RequestBody PaymentRequestDto paymentRequestDto) {
      // 요청 데이터 확인
      System.out.println("결제 요청 데이터: " + paymentRequestDto);
      
      // 회원 조회 및 구독 상태 업데이트
      Member member = memberRepository.findByEmail(paymentRequestDto.getBuyerEmail())
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원을 찾을 수 없습니다: " + paymentRequestDto.getBuyerEmail()));
      member.setSubscription(paymentRequestDto.getSubscription()); // 구독 상태 업데이트
      member.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
      
      
      // MembershipPlan 생성 및 설정
      MembershipPlan membershipPlan = new MembershipPlan();
      membershipPlan.setNextPaymentDate(LocalDate.now().plusMonths(1)); // 다음 결제일 설정
      membershipPlan.setSubscriptionStatus(SubscriptionStatus.ACTIVE); // 구독 상태 설정
      
      watchHistoryService.deleteWatchHistoryByMemberId(member.getId());
      
      
      // Payment 생성 및 설정
      String merchantUid = "SUBSCRIPTION_" + UUID.randomUUID(); // 고유한 merchant_uid 생성
      Payment payment = new Payment();
      payment.setCustomerUid(paymentRequestDto.getCustomerUid());
      payment.setSubscribeNm(paymentRequestDto.getSubscribeNm());
      payment.setImpUid(paymentRequestDto.getImpUid());
      payment.setMerchantUid(paymentRequestDto.getMerchantUid());
      payment.setMerchantUid(merchantUid); // 고유한 merchant_uid 설정
      payment.setPrice(paymentRequestDto.getPrice());
      payment.setStatus(paymentRequestDto.getStatus());
      payment.setBuyerName(paymentRequestDto.getBuyerName());
      payment.setBuyerEmail(paymentRequestDto.getBuyerEmail());
      payment.setBuyerTel(paymentRequestDto.getBuyerTel());
      payment.setPaymentDate(LocalDateTime.now());
      payment.setSubscription(paymentRequestDto.getSubscription());


      // 환불 가능 여부 초기화
      payment.setRefundable(true); // 기본값: 환불 가능
      
      // 양방향 관계 설정
      payment.setMembershipPlan(membershipPlan);
      membershipPlan.setPayment(payment);
      
      // 서비스 호출로 결제 정보와 MembershipPlan 저장
      paymentService.savePaymentWithMembershipPlan(payment, membershipPlan);
      
      
      memberRepository.save(member);

      // 정기결제 스케줄 등록
      System.out.println("정기결제 스케줄 등록을 시작합니다.");
      boolean isScheduled = paymentService.processSubscriptionPayment(
              paymentRequestDto.getCustomerUid(),
              paymentRequestDto.getPrice(),
              merchantUid
      );
      // 스케줄 등록 결과 로그
      if (isScheduled) {
         System.out.println("정기결제 스케줄이 성공적으로 등록되었습니다.");
      } else {
         System.out.println("정기결제 스케줄 등록에 실패했습니다.");
      }


      return "결제 정보가 성공적으로 저장되었습니다.";
   }
   
   
   @GetMapping("/payment/success")
   public String paymentSuccess(Model model) {
      // 필요한 데이터 설정 (예: 사용자 정보, 결제 정보)
      model.addAttribute("message", "결제가 성공적으로 완료되었습니다!");
      model.addAttribute("redirectUrl", "/orders/orderdetail"); // 주문 상세 페이지로 이동
      
      return "payment/success"; // HTML 파일 이름
   }
   
   @GetMapping("/payment/failure")
   public String paymentFailure(Model model) {
      model.addAttribute("message", "결제가 실패하였습니다. 다시 시도해주세요.");
      model.addAttribute("retryUrl", "/subscribe/buy"); // 결제 재시도 페이지로 이동
      return "payment/failure"; // 실패 화면 HTML 파일 이름
   }
   
   
   @GetMapping("/order/orderList")
   public String getPaymentHistory(Principal principal, @PageableDefault(size = 10) Pageable pageable, Model model) {
      // 인증된 사용자의 이메일 가져오기
      String email = SecurityUtil.getCurrentUserEmail();
      
      if (email == null) {
         throw new IllegalStateException("로그인된 사용자의 이메일 정보를 가져올 수 없습니다.");
      }
      
      Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
      
      // 결제 이력 데이터를 서비스에서 가져오기
      Page<PaymentRequestDto> paymentHistory = paymentService.getPaymentHistoryByEmail(email, pageable);
      
      if (paymentHistory.isEmpty()) {
         paymentHistory = new PageImpl<>(new ArrayList<>(), pageable, 1); // 빈 페이지 생성
      }
      
      List<PaymentRequestDto> updatedPayments = paymentHistory.getContent().stream()
            .map(paymentDto -> {
               // paymentDto의 데이터를 사용하여 환불 가능 여부 계산
               boolean hasViewHistory = watchHistoryService.hasViewHistory(member);
               boolean isWithin3Days = Duration.between(paymentDto.getPaymentDate(), LocalDateTime.now()).toDays() < 3;
               boolean refundable = isWithin3Days && !hasViewHistory;
               
               paymentDto.setRefundable(refundable); // DTO에 환불 가능 여부 설정
               return paymentDto;
            })
            .toList();
      
      // 모델에 데이터 추가
      model.addAttribute("payments", paymentHistory);
      model.addAttribute("email", email);
      
      // HTML 템플릿 반환
      return "order/orderList";
   }
   
   
   @PostMapping("/payment/refund")
   public ResponseEntity<String> refundPayment(@RequestBody Map<String, String> request) {
      try {
         String impUid = request.get("impUid");
         System.out.println("확인용 impUid : " + impUid);
         String result = refundService.processRefundByImpUid(impUid);
         System.out.println("확인용 result :" + result);
         return ResponseEntity.ok(result);
      } catch (RuntimeException e) {
         return ResponseEntity.badRequest().body(e.getMessage());
      } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("환불 처리 중 예상치 못한 오류가 발생했습니다.");
      }
   }

   @PostMapping("/schedule")
   public ResponseEntity<String> scheduleSubscriptionPayment(@RequestBody PaymentRequestDto requestDto) {
      boolean isSuccess = paymentService.processSubscriptionPayment(
              requestDto.getCustomerUid(),
              requestDto.getAmount(),
              requestDto.getMerchantUid()
      );
      if (isSuccess) {
         return ResponseEntity.ok("정기결제가 성공적으로 등록되었습니다.");
      } else {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                 .body("정기결제 등록 중 오류가 발생했습니다.");
      }
   }
   @PostMapping("/iamport/webhook")
   public ResponseEntity<String> handleIamportWebhook(@RequestBody Map<String, Object> payload) {
      String impUid = (String) payload.get("imp_uid");
      String status = (String) payload.get("status");
      // impUid를 통해 결제 정보 확인 및 상태 업데이트 처리
      System.out.println("Webhook received: impUid = " + impUid + ", status = " + status);
      return ResponseEntity.ok("Webhook processed.");
   }
   @GetMapping("/subscription/status")
   public String getSubscriptionStatus(Model model) {
      List<ScheduledPayment> scheduledPayments = scheduledPaymentRepository.findAll();
      model.addAttribute("scheduledPayments", scheduledPayments);
      return "subscription/status";
   }
}
