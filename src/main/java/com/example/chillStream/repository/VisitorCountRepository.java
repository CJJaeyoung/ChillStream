package com.example.chillStream.repository;

import com.example.chillStream.entity.VisitorCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface VisitorCountRepository extends JpaRepository<VisitorCount, Long> {
    Optional<VisitorCount> findByVisitDate(LocalDate date);
} 