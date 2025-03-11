package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long amount;
    private LocalDateTime createdDate;
    
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }
} 