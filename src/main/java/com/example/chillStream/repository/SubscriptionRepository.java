package com.example.chillStream.repository;

import com.example.chillStream.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByCreatedDateBetween(LocalDateTime start, LocalDateTime end);
} 