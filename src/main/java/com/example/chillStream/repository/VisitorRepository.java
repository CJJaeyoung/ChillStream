package com.example.chillStream.repository;

import com.example.chillStream.entity.VisitorCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface VisitorRepository extends JpaRepository<VisitorCount, Long> {

    VisitorCount findByVisitDate(LocalDate date);

    @Query("SELECT v FROM VisitorCount v WHERE v.visitDate >= :startDate AND v.visitDate <= :endDate ORDER BY v.visitDate")
    List<VisitorCount> findVisitorStatsByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', v.visitDate, '%Y-%m') as month, SUM(v.dailyCount) as total " +
            "FROM VisitorCount v " +
            "GROUP BY FUNCTION('DATE_FORMAT', v.visitDate, '%Y-%m') " +
            "ORDER BY month DESC " +
            "LIMIT 6")
    List<Object[]> findMonthlyStats();

    @Query("SELECT v.visitDate as date, v.dailyCount as count " +
            "FROM VisitorCount v " +
            "WHERE v.visitDate >= :startDate AND v.visitDate <= :endDate " +
            "ORDER BY v.visitDate DESC")
    List<Object[]> findDailyStats(LocalDate startDate, LocalDate endDate);
}