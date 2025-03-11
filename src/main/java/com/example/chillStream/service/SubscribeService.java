package com.example.chillStream.service;

import com.example.chillStream.dto.SubscribeFormDto;
import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.Subscribe;
import com.example.chillStream.constant.Subscription;
import com.example.chillStream.entity.Payment;
import com.example.chillStream.repository.MemberRepository;
import com.example.chillStream.repository.SubscribeRepository;
import com.example.chillStream.repository.SubscriptionRepository;
import com.example.chillStream.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
@Service
@Transactional
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public Subscribe findById(Long id) {
        return subscribeRepository.findById(id).orElse(null);
    }

    public Long saveSubscribe(SubscribeFormDto subscribeFormDto)
            throws Exception {
        // 상품 등록
        Subscribe subscribe = subscribeFormDto.createSubscribe();
        subscribeRepository.save(subscribe);


        return subscribe.getId();
    }
    
    // 수정하기 전 불러오기
    @Transactional(readOnly = true) // 읽기 전용 더티체킹(변경감지)
    public SubscribeFormDto getSubscribeEdit(Long subscribeId){
        Subscribe subscribe = subscribeRepository.findById(subscribeId).orElseThrow(EntityNotFoundException::new);
        SubscribeFormDto subscribeFormDto = SubscribeFormDto.of(subscribe);
        return subscribeFormDto;
    }


    public Long updateSubscribe(SubscribeFormDto subscribeFormDto)
            throws Exception {
        //상품 변경
        Subscribe subscribe = subscribeRepository.findById(subscribeFormDto.getId()).
                orElseThrow(EntityNotFoundException::new);
        subscribe.updateSubscribe(subscribeFormDto);

        return subscribe.getId();
    }
    
    public void deleteSubscribe(Long id) {
        Subscribe subscribe = subscribeRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException("구독 정보를 찾을 수 없습니다."));
        subscribeRepository.delete(subscribe);
    }

    public List<SubscribeFormDto> getAllSubscribeDtos() {
        List<Subscribe> subscribes = subscribeRepository.findAll();
        List<SubscribeFormDto> dtoList = new ArrayList<>();

        for (Subscribe subscribe : subscribes) {
            SubscribeFormDto dto = new SubscribeFormDto();
            dto.setId(subscribe.getId());
            dto.setSubscribeNm(subscribe.getSubscribeNm());
            dto.setSubscription(subscribe.getSubscription());
            dto.setSubscribeDetail(subscribe.getSubscribeDetail());
            dto.setPrice(subscribe.getPrice());

            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<Subscribe> getAllSubscribes() {
        return subscribeRepository.findAll();
    }
    
    
    public String checkSubscription(String email) throws ChangeSetPersister.NotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(ChangeSetPersister.NotFoundException::new);
        String membersSubs = member.getSubscription().toString();
        System.out.println(membersSubs);
        return membersSubs;
    }

    public void processPayment(Long amount) {
        // 기존 결제 처리 코드...
        
        // TODO: 필요한 경우 다른 방식으로 매출 데이터 저장 구현
        
    }
    
    
    
    // 총 매출 계산 메서드
    @Transactional(readOnly = true)
    public Long calculateTotalRevenue() {
        return paymentRepository.findAll().stream()
              .filter(payment -> payment.getSubscription() == Subscription.BASIC ||
                    payment.getSubscription() == Subscription.PREMIUM)
              .mapToLong(Payment::getPrice)
              .sum();
    }
    
    // 주간 매출 계산 메서드
    @Transactional(readOnly = true)
    public Map<String, Long> calculateWeeklyRevenue() {
        LocalDateTime now = LocalDateTime.now();
        // 오늘 자정을 기준으로 설정
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        LocalDateTime weekAgo = now.minusDays(7).withHour(0).withMinute(0).withSecond(0);
        
        return paymentRepository.findAll().stream()
              .filter(payment -> (payment.getSubscription() == Subscription.BASIC ||
                    payment.getSubscription() == Subscription.PREMIUM)
                    && payment.getPaymentDate().isAfter(weekAgo)
                    && payment.getPaymentDate().isBefore(endOfDay))
              .collect(Collectors.groupingBy(
                    payment -> payment.getPaymentDate().toLocalDate().toString(),
                    Collectors.summingLong(Payment::getPrice)
              ));
    }
    // 일일 매출 계산 메서드 추가
    @Transactional(readOnly = true)
    public Map<String, Long> calculateDailyRevenue() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = now.withHour(23).withMinute(59).withSecond(59);
        
        Map<String, Long> dailyRevenue = new LinkedHashMap<>(); // 순서 유지를 위해 LinkedHashMap 사용
        
        for (int i = 6; i >= 0; i--) {
            LocalDateTime currentDay = startOfDay.minusDays(i);
            LocalDateTime nextDay = endOfDay.minusDays(i);
            
            Long revenue = paymentRepository.findAll().stream()
                  .filter(payment -> (payment.getSubscription() == Subscription.BASIC ||
                        payment.getSubscription() == Subscription.PREMIUM)
                        && payment.getPaymentDate().isAfter(currentDay)
                        && payment.getPaymentDate().isBefore(nextDay))
                  .mapToLong(Payment::getPrice)
                  .sum();
            
            dailyRevenue.put(currentDay.toLocalDate().toString(), revenue);
        }
        
        return dailyRevenue;
    }
}