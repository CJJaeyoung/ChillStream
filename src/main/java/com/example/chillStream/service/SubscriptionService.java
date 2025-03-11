package com.example.chillStream.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.example.chillStream.entity.Subscription;
import com.example.chillStream.repository.SubscriptionRepository;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public Map<String, Long> getWeeklyRevenue() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusWeeks(1);
        
        // 최근 7일간의 매출 데이터 조회
        List<Subscription> subscriptions = subscriptionRepository
            .findByCreatedDateBetween(weekAgo, now);
        
        // 일자별 매출 집계
        Map<String, Long> dailyRevenue = new TreeMap<>();
        
        // 최근 7일 날짜 초기화
        for (int i = 0; i < 7; i++) {
            LocalDateTime date = now.minusDays(i);
            dailyRevenue.put(
                date.format(DateTimeFormatter.ofPattern("MM/dd")),
                0L
            );
        }
        
        // 실제 매출 데이터 집계
        subscriptions.forEach(subscription -> {
            String dateKey = subscription.getCreatedDate()
                .format(DateTimeFormatter.ofPattern("MM/dd"));
            Long amount = subscription.getAmount();
            dailyRevenue.merge(dateKey, amount, Long::sum);
        });
        
        return dailyRevenue;
    }

    @PostConstruct  // 애플리케이션 시작 시 실행
    public void initTestData() {
        // 테스트 데이터가 없을 경우에만 생성
        if (subscriptionRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();
            
            // 최근 7일간의 테스트 데이터 생성
            for (int i = 0; i < 7; i++) {
                Subscription subscription = new Subscription();
                subscription.setAmount(10000L + (i * 1000));  // 10000원부터 1000원씩 증가
                subscription.setCreatedDate(now.minusDays(i));
                subscriptionRepository.save(subscription);
            }
        }
    }

    public Long getTotalRevenue() {
        return subscriptionRepository.findAll().stream()
                .mapToLong(Subscription::getAmount)
                .sum();
    }

    public Long getWeeklyTotalRevenue() {
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        return subscriptionRepository.findByCreatedDateBetween(weekAgo, LocalDateTime.now()).stream()
                .mapToLong(Subscription::getAmount)
                .sum();
    }
} 