package com.example.chillStream.service;

import com.example.chillStream.entity.VisitorCount;
import com.example.chillStream.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorService {

    private final VisitorRepository visitorRepository;

    public void incrementVisitorCount() {
        LocalDate today = LocalDate.now();
        VisitorCount visitorCount = visitorRepository.findByVisitDate(today);

        if (visitorCount == null) {
            visitorCount = new VisitorCount();
            visitorCount.setVisitDate(today);
            visitorCount.setDailyCount(1);
            visitorCount.setTotalCount(getTotalVisitorCount() + 1);
        } else {
            visitorCount.setDailyCount(visitorCount.getDailyCount() + 1);
            visitorCount.setTotalCount(visitorCount.getTotalCount() + 1);
        }

        visitorRepository.save(visitorCount);
    }

    public long getTotalVisitorCount() {
        return visitorRepository.findAll().stream()
                .mapToLong(VisitorCount::getDailyCount)
                .sum();
    }

    public Map<String, Long> getMonthlyStats() {
        Map<String, Long> monthlyStats = new LinkedHashMap<>();

        visitorRepository.findMonthlyStats().forEach(result -> {
            String month = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            monthlyStats.put(month, count);
        });

        return monthlyStats;
    }

    public Map<String, Long> getDailyStats() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);  // 최근 30일 데이터

        Map<String, Long> dailyStats = new LinkedHashMap<>();

        visitorRepository.findDailyStats(startDate, endDate).forEach(result -> {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            dailyStats.put(date.toString(), count);
        });

        return dailyStats;
    }

    public long getTodayVisitorCount() {
        LocalDate today = LocalDate.now();
        VisitorCount visitorCount = visitorRepository.findByVisitDate(today);
        return visitorCount != null ? visitorCount.getDailyCount() : 0;
    }

    public Map<String, Long> getRecentDailyStats() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // ��근 7일

        Map<String, Long> dailyStats = new LinkedHashMap<>();
        List<VisitorCount> visitors = visitorRepository.findVisitorStatsByDateRange(startDate, today);

        // 모든 날짜에 ��해 0으로 초기화
        for (int i = 0; i <= 6; i++) {
            LocalDate date = today.minusDays(i);
            dailyStats.put(date.toString(), 0L);
        }

        // 실제 데이터로 업데이트
        visitors.forEach(visitor ->
                dailyStats.put(visitor.getVisitDate().toString(), (long) visitor.getDailyCount())
        );

        return dailyStats;
    }
}