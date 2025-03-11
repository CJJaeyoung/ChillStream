package com.example.chillStream.service;


import com.example.chillStream.constant.Subscription;
import com.example.chillStream.constant.SubscriptionStatus;
import com.example.chillStream.dto.PaymentRequestDto;
import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.MembershipPlan;
import com.example.chillStream.entity.Payment;
import com.example.chillStream.repository.MemberRepository;
import com.example.chillStream.repository.MembershipPlanRepository;
import com.example.chillStream.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class RefundService {
   
   private final PaymentRepository paymentRepository;
   private final MemberRepository memberRepository;
   private final MembershipPlanRepository membershipPlanRepository;
   
   private final String IMP_KEY = "PortOne API Key";
   private final String IMP_SECRET = "PortOne Secret Key";
   
   public String processRefundByImpUid(String impUid) {
      Optional<Payment> optionalPayment = paymentRepository.findByImpUid(impUid);
      if (optionalPayment.isEmpty()) {
         return "해당 결제를 찾을 수 없습니다. 결제 ID: " + impUid;
      }
      
      Payment payment = optionalPayment.get();
      
      if (!"paid".equals(payment.getStatus())) {
         return "환불할 수 없는 상태입니다. 현재 상태: " + payment.getStatus();
      }
      
      // 환불 가능 여부 확인
      if (!payment.isRefundable()) {
         return "환불이 불가능한 결제입니다. 결제 ID: " + impUid;
      }
      
      // 포트원 환불 처리 호출
      boolean refundSuccess = requestRefundFromPortOne(payment);
      if (refundSuccess) {
         payment.setStatus("REFUNDED");
         payment.setRefundable(false); // 환불 불가로 설정
         paymentRepository.save(payment);
         
         updateMemberSubscriptionToInactive(payment.getBuyerEmail());
         
         return "환불이 성공적으로 처리되었습니다.";
      }
      
      return "환불 처리에 실패했습니다. 결제 ID: " + impUid;
   }
   
   private boolean requestRefundFromPortOne(Payment payment) {
      try {
         // 1. 포트원 액세스 토큰 발급
         String accessToken = getAccessToken();
         if (accessToken == null) {
            System.err.println("[ERROR] 포트원 액세스 토큰 발급 실패");
            return false;
         }
         
         // 2. 환불 요청
         String url = "https://api.iamport.kr/payments/cancel";
         
         // HTTP 요청 설정
         CloseableHttpClient httpClient = HttpClients.createDefault();
         HttpPost httpPost = new HttpPost(url);
         
         JsonObject json = new JsonObject();
         json.addProperty("imp_uid", payment.getImpUid());
         json.addProperty("reason", "사용자 요청에 의한 환불");
         json.addProperty("amount", payment.getPrice());
         
         StringEntity entity = new StringEntity(json.toString(), "UTF-8");
         httpPost.setEntity(entity);
         httpPost.setHeader("Content-Type", "application/json");
         httpPost.setHeader("Authorization", accessToken);
         
         // HTTP 요청 실행
         try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            
            int responseCode = responseJson.get("code").getAsInt();
            if (responseCode != 0) {
               String errorMessage = responseJson.get("message").getAsString();
               System.err.println("[ERROR] Refund failed: " + errorMessage);
               return false;
            }
            
            return true; // 성공 여부 반환
         }
      } catch (Exception e) {
         System.err.println("[ERROR] 환불 처리 중 오류 발생: " + e.getMessage());
         e.printStackTrace();
         return false;
      }
   }
   
   private String getAccessToken() throws Exception {
      String url = "https://api.iamport.kr/users/getToken";
      
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpPost httpPost = new HttpPost(url);
      
      JsonObject json = new JsonObject();
      json.addProperty("imp_key", IMP_KEY);
      json.addProperty("imp_secret", IMP_SECRET);
      
      StringEntity entity = new StringEntity(json.toString(), "UTF-8");
      httpPost.setEntity(entity);
      httpPost.setHeader("Content-Type", "application/json");
      
      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
         String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
         JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
         
         if (!responseJson.has("response") || responseJson.get("response").isJsonNull()) {
            System.err.println("[ERROR] 액세스 토큰 응답 데이터가 잘못되었습니다.");
            return null;
         }
         
         JsonObject responseData = responseJson.getAsJsonObject("response");
         if (!responseData.has("access_token") || responseData.get("access_token").isJsonNull()) {
            System.err.println("[ERROR] 액세스 토큰이 응답 데이터에 없습니다.");
            return null;
         }
         
         return responseData.get("access_token").getAsString();
      }
   }


   /**
    * 데이터베이스에서 환불 상태 업데이트
    */
   private void updateRefundRecord(PaymentRequestDto paymentRequestDto) {
      // 상태를 "REFUNDED"로 변경
      paymentRequestDto.setStatus("REFUNDED");
      paymentRequestDto.setSubscriptionEndDate(null); // 구독 해지 처리
      // TODO: 실제 데이터베이스 업데이트 로직 추가 필요
   }
   
   private void updateMemberSubscriptionToInactive(String email) {
      Optional<Member> optionalMember = memberRepository.findByEmail(email);
      if (optionalMember.isPresent()) {
         Member member = optionalMember.get();
         
         // 디버깅: Member 초기 상태 출력
         System.out.println("변경 전 Member.subscription: " + member.getSubscription());
         System.out.println("변경 전 Member.subscriptionStatus: " + member.getSubscriptionStatus());
         
         // Member의 Subscription 상태를 INACTIVE로 변경
         member.setSubscription(Subscription.INACTIVE);
         
         // Member의 SubscriptionStatus 상태를 CANCELLED로 변경
         member.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
         
         // MembershipPlan 강제 업데이트
         if (member.getMembershipPlan() != null) {
            MembershipPlan membershipPlan = member.getMembershipPlan();
            System.out.println("삭제할 MembershipPlan ID: " + membershipPlan.getId());
            
            // Member와 MembershipPlan 간의 관계 해제
            member.setMembershipPlan(null);
            membershipPlan.setMember(null);
            
            // MembershipPlan 삭제
            membershipPlanRepository.delete(membershipPlan);
            System.out.println("MembershipPlan이 삭제되었습니다.");
         } else {
            System.err.println("MembershipPlan이 존재하지 않습니다. Email: " + email);
         }
         
         // 변경된 Member 저장
         memberRepository.save(member);
         System.out.println("회원의 구독 상태가 INACTIVE로 변경되었습니다. Email: " + email);
      } else {
         System.err.println("해당 이메일을 가진 회원을 찾을 수 없습니다. Email: " + email);
      }
   }
   
   
}
