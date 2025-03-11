package com.example.chillStream.entity;

import com.example.chillStream.constant.Role;
import com.example.chillStream.constant.Subscription;
import com.example.chillStream.constant.SubscriptionStatus;
import com.example.chillStream.dto.MemberFormDto;
import com.example.chillStream.dto.MemberPasswordDto;
import com.example.chillStream.dto.MemberUpdateFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String tel;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    private String provider; // 소셜 로그인 제공자 정보 (예: Google, Facebook 등)
    
    private String picture; // 소셜 로그인 사용자를 위한 필드

    @Enumerated(EnumType.STRING)
    private Subscription subscription;
    
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.CANCELLED; // 기본값은 미구독 상태
    
    @Column(name = "first_login", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean firstLogin;


    private String passwordResetToken;
    private LocalDateTime tokenExpiryDate;
    
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true) // 수정된 mappedBy 속성
    private MembershipPlan membershipPlan;
    
    // 사용자 선택 데이터와의 연관 관계 추가
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMovieSelection> movieSelections = new ArrayList<>();

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setTel(memberFormDto.getTel());
        member.setRole(Role.ADMIN);
        member.setSubscription(Subscription.INACTIVE);
        member.setFirstLogin(true);

        return member;
    }
    public Member update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }
    
    // 소셜 로그인 사용자 생성자
    @Builder
    public Member(Long id,String name, String email, String picture, String provider, Role role, String password,Subscription subscription, boolean firstLogin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;
        this.role = role;
        this.password = password != null ? password : "OAUTH_USER"; // 기본값 설정
        this.subscription = subscription;
        this.firstLogin = firstLogin;
    }
    
    // 정식 회원 정보 업데이트 메서드
    public void updateMember(MemberUpdateFormDto memberUpdateFormDto) {
        this.tel = memberUpdateFormDto.getTel();
    }
    
    // 정식 회원 비밀번호 업데이트 메서드
    public void updateMemberPassword(MemberPasswordDto memberPasswordDto,
                                     PasswordEncoder passwordEncoder) {
        String newPassword = passwordEncoder.encode(memberPasswordDto.getPassword());
        this.password = newPassword;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
    
    
    public void updateFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
}
