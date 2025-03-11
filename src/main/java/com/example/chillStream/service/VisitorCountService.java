package com.example.chillStream.service;

import com.example.chillStream.entity.VisitorCount;
import com.example.chillStream.repository.VisitorCountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VisitorCountService {
    
    private final VisitorCountRepository visitorCountRepository;

    @Transactional
    public void incrementVisitorCount() {
        LocalDate today = LocalDate.now();
        VisitorCount visitorCount = visitorCountRepository.findByVisitDate(today)
                .orElse(new VisitorCount());

        if (visitorCount.getVisitDate() == null) {
            visitorCount.setVisitDate(today);
            visitorCount.setDailyCount(0);
        }

        visitorCount.setDailyCount(visitorCount.getDailyCount() + 1);
        visitorCount.setTotalCount(visitorCount.getTotalCount() + 1);
        
        visitorCountRepository.save(visitorCount);
    }

    public int getTodayVisitorCount() {
        return visitorCountRepository.findByVisitDate(LocalDate.now())
                .map(VisitorCount::getDailyCount)
                .orElse(0);
    }

    public long getTotalVisitorCount() {
        return visitorCountRepository.findAll().stream()
                .mapToLong(VisitorCount::getDailyCount)
                .sum();
    }
} 