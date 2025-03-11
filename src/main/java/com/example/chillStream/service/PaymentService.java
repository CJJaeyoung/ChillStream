package com.example.chillStream.service;

import com.example.chillStream.constant.Subscription;
import com.example.chillStream.dto.PaymentRequestDto;
import com.example.chillStream.entity.*;
import com.example.chillStream.repository.MembershipPlanRepository;
import com.example.chillStream.repository.PaymentRepository;
import com.example.chillStream.repository.ScheduledPaymentRepository;
import com.example.chillStream.repository.WatchHistoryRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.ScheduleData;
import com.siot.IamportRestClient.request.ScheduleEntry;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Schedule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Environment environment;
    private final MembershipPlanRepository membershipPlanRepository;
    private final WatchHistoryService watchHistoryService;
    private final IamportClient iamportClient;
    private final ScheduledPaymentRepository scheduledPaymentRepository;

    @Transactional
    public void savePaymentWithMembershipPlan(Payment payment, MembershipPlan membershipPlan) {
        // MembershipPlan을 먼저 저장
        membershipPlanRepository.save(membershipPlan);

        // Payment와 MembershipPlan 간의 관계 설정
        payment.setMembershipPlan(membershipPlan);

        // Payment 저장
        paymentRepository.save(payment);
    }

    public void savePayment(String subscribeNm, String impUid, String merchantUid, int price, String status,
                            String buyerName, String buyerEmail, String buyerTel, Subscription subscription) {


        Payment payment = new Payment(
                subscribeNm,
                impUid,
                merchantUid,
                price,
                status,
                buyerName,
                buyerEmail,
                buyerTel,
                LocalDateTime.now(),
                subscription
        );

        paymentRepository.save(payment);

    }


    public Page<PaymentRequestDto> getPaymentHistoryByEmail(String email, Pageable pageable) {
        return paymentRepository.findByBuyerEmailSorted(email, pageable)
                .map(payment -> {
                    PaymentRequestDto dto = new PaymentRequestDto();
                    dto.setSubscribeNm(payment.getSubscribeNm());
                    dto.setImpUid(payment.getImpUid());
                    dto.setMerchantUid(payment.getMerchantUid());
                    dto.setPrice(payment.getPrice());
                    dto.setStatus(payment.getStatus());
                    dto.setBuyerName(payment.getBuyerName());
                    dto.setBuyerEmail(payment.getBuyerEmail());
                    dto.setBuyerTel(payment.getBuyerTel());
                    dto.setPaymentDate(payment.getPaymentDate());
                    dto.setSubscriptionEndDate(payment.getPaymentDate().plusMonths(1));
                    dto.setSubscription(payment.getSubscription()); // 구독 상태 추가

                    return dto;
                });
    }


    // 정기 결제 처리
    public void processRecurringPayment(MembershipPlan plan) {
        // 결제 게이트웨이를 통해 정기 결제 처리
        boolean paymentSuccess = requestRecurringPayment(plan);

        if (paymentSuccess) {
            savePayment(
                    plan.getPayment().getSubscribeNm(),
                    "generated-impUid", // 실제 API 호출로 받은 impUid
                    "generated-merchantUid", // 실제 API 호출로 받은 merchantUid
                    plan.getPayment().getPrice(),
                    "결제완료",
                    plan.getPayment().getBuyerName(),
                    plan.getPayment().getBuyerEmail(),
                    plan.getPayment().getBuyerTel(),
                    plan.getPayment().getSubscription()
            );
        } else {
            throw new RuntimeException("정기 결제 실패");
        }
    }


    public boolean requestRecurringPayment(MembershipPlan plan) {
        String apiUrl = "https://api.iamport.kr/subscribe/payments/again";
        String accessToken = getIamportAccessToken();

        RestTemplate restTemplate = new RestTemplate();

        // 올바른 HttpHeaders 사용
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 데이터 설정
        Map<String, Object> request = new HashMap<>();
        request.put("customer_uid", plan.getPayment().getBuyerEmail()); // 고객 식별자
        request.put("amount", plan.getPayment().getPrice()); // 결제 금액
        request.put("name", plan.getPayment().getSubscribeNm()); // 구독 이름

        // HttpEntity 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            // API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            return response.getStatusCode().is2xxSuccessful(); // 성공 여부 반환
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // 결제 실패
    }

    private String getIamportAccessToken() {
        String apiUrl = "https://api.iamport.kr/users/getToken";

        RestTemplate restTemplate = new RestTemplate();

        // 올바른 HttpHeaders 사용
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 데이터 설정
        Map<String, String> request = new HashMap<>();
        request.put("imp_key", environment.getProperty("iamport.api.key")); // 프로퍼티에서 읽은 API Key
        request.put("imp_secret", environment.getProperty("iamport.api.secret")); // 프로퍼티에서 읽은 Secret Key

        // HttpEntity 생성
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        try {
            // API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = (Map<String, Object>) response.getBody().get("response");
                return (String) body.get("access_token"); // 액세스 토큰 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Iamport 토큰 발급 실패");
        }

        throw new RuntimeException("Iamport 인증 실패");
    }


    public boolean calculateRefundable(Payment payment, Member member, Video video) {
        // 결제가 완료되지 않은 경우
        if (!"paid".equals(payment.getStatus())) {
            return false;
        }

        // 이미 환불 불가로 설정된 결제인 경우
        if (!payment.isRefundable()) {
            return false;
        }

        // 시청 기록 확인
        boolean hasViewHistory = watchHistoryService.hasViewHistory(member);

        // 3일 이내 확인
        boolean isWithin3Days = Duration.between(payment.getPaymentDate(), LocalDateTime.now()).toDays() < 3;

        // 환불 가능 여부
        return isWithin3Days && !hasViewHistory;
    }


    @Transactional
    public boolean processSubscriptionPayment(String customerUid, int amount, String merchantUid) {
        try {
            ScheduleData scheduleData = new ScheduleData(customerUid);
            Date scheduledDate = Date.from(LocalDateTime.now().plusMonths(1)
                    .atZone(ZoneId.systemDefault()).toInstant());
            scheduleData.addSchedule(new ScheduleEntry(merchantUid, scheduledDate, BigDecimal.valueOf(amount)));
            IamportResponse<List<Schedule>> response = iamportClient.subscribeSchedule(scheduleData);
            if (response.getResponse() != null) {
                // 스케줄 정보를 데이터베이스에 저장
                for (Schedule schedule : response.getResponse()) {
                    ScheduledPayment scheduledPayment = new ScheduledPayment();
                    scheduledPayment.setCustomerUid(customerUid);
                    scheduledPayment.setMerchantUid(schedule.getMerchantUid());
                    scheduledPayment.setAmount(schedule.getAmount().intValue());
                    scheduledPayment.setScheduledDate(schedule.getScheduleAt());
                    // 저장 전 디버깅 로그
                    System.out.println("저장할 데이터: " + scheduledPayment);
                    // 데이터 저장
                    scheduledPaymentRepository.save(scheduledPayment);
                    // 저장 후 디버깅 로그
                    System.out.println("스케줄 저장 성공: " + scheduledPayment);
                }
                return true;
            } else {
                System.out.println("정기결제 스케줄 등록 실패: " + response.getMessage());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}