package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "scheduled_payment")
public class ScheduledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerUid;
    private String merchantUid;
    private int amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date scheduledDate;
}