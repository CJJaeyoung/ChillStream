package com.example.chillStream.repository;

import com.example.chillStream.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Member findByTel(String tel);
    
    
    Optional<Member> findByNameAndTel(String name, String tel);
    
    Optional<Member> findByPasswordResetToken(String token);

}
