package com.example.chillStream.repository;

import com.example.chillStream.entity.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment, Long> {
}