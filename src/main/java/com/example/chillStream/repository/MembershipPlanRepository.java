package com.example.chillStream.repository;

import com.example.chillStream.constant.SubscriptionStatus;
import com.example.chillStream.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByNextPaymentDate(LocalDate nextPaymentDate);
    
    @Modifying
    @Query("UPDATE MembershipPlan mp SET mp.subscriptionStatus = :status WHERE mp.member.id = :memberId")
    void updateSubscriptionStatusByMemberId(@Param("status") SubscriptionStatus status, @Param("memberId") Long memberId);
    
    
}
