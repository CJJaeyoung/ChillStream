package com.example.chillStream.service;

import com.example.chillStream.constant.Subscription;
import com.example.chillStream.constant.SubscriptionStatus;
import com.example.chillStream.entity.MembershipPlan;
import com.example.chillStream.entity.Payment;
import com.example.chillStream.repository.MembershipPlanRepository;
import com.example.chillStream.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final PaymentRepository paymentRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentService paymentService;



    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void updateSubscriptionStatus() {
        LocalDateTime now = LocalDateTime.now();

        // BASIC 구독 상태가 한 달이 지난 경우 INACTIVE로 변경
        List<Payment> basicExpired = paymentRepository.findBySubscriptionAndPaymentDateBefore(Subscription.BASIC, now.minusMonths(1));
        basicExpired.forEach(payment -> payment.setSubscription(Subscription.INACTIVE));

        // PREMIUM 구독 상태가 한 달이 지난 경우 INACTIVE로 변경
        List<Payment> premiumExpired = paymentRepository.findBySubscriptionAndPaymentDateBefore(Subscription.PREMIUM, now.minusMonths(1));
        premiumExpired.forEach(payment -> payment.setSubscription(Subscription.INACTIVE));

        // 상태 업데이트
        paymentRepository.saveAll(basicExpired);
        paymentRepository.saveAll(premiumExpired);
    }


    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processRecurringPayments() {
        LocalDate today = LocalDate.now();

        // 오늘 결제일인 구독을 조회
        List<MembershipPlan> duePlans = membershipPlanRepository.findByNextPaymentDate(today);

        for (MembershipPlan plan : duePlans) {
            try {
                // 정기 결제 처리
                paymentService.processRecurringPayment(plan);

                // 결제가 성공하면 다음 결제일 업데이트
                plan.setNextPaymentDate(today.plusMonths(1));
                membershipPlanRepository.save(plan);

            } catch (Exception e) {
                // 결제가 실패하면 실패 횟수 증가
                plan.setFailedAttempts(plan.getFailedAttempts() + 1);

                // 실패가 3회 이상이면 구독 취소
                if (plan.getFailedAttempts() >= 3) {
                    plan.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
                }

                membershipPlanRepository.save(plan);
            }
        }
    }
}