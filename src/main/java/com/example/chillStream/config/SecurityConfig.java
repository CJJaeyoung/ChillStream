package com.example.chillStream.config;

import com.example.chillStream.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
   
   @Autowired
   MemberService memberService;
   
   @Autowired
   private CustomOAuth2UserService customOAuth2UserService;
   
   @Bean // 스프링 컨테이너에 올라가는 객체
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.csrf(csrf -> csrf
            .ignoringRequestMatchers("/ws/**", "/topic/**", "/app/**", "/members/login")
            .ignoringRequestMatchers("/create-room", "/chat/**", "/admin/**", "/check-email", "/api/chatbot/**")
            .ignoringRequestMatchers("/home/**", "/movies/**", "/save-selection")
            .ignoringRequestMatchers("/payment/**")
            .ignoringRequestMatchers("/members/find-email", "/members/find-password")
            .ignoringRequestMatchers("/members/reset-password")
            
      
      ).authorizeHttpRequests(auth -> auth // 인증관리
            .requestMatchers("/save-selection").authenticated()
            .requestMatchers("/css/**", "/js/**", "/img/**", "favicon.ico", "/error", "/check-email",
                  "/google-logo.png", "/kakao-logo.png", "/naver-logo.png").permitAll()
            .requestMatchers("/", "/members/**", "/find-email","/members/find-password", "/subscribe/**", "/images/**", "/mail/**", "/chat/**",
                  "/create-room", "/api/chatbot/**", "/home/**", "/movies/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/translate").permitAll()
            .anyRequest().authenticated()
      
      ).formLogin(formLogin -> formLogin
            .loginPage("/members/login")
            .successHandler((request, response, authentication) -> {
               //로그인 성공 후 사용자 역활에 따라 다른 페이지로 리다이렉트된다.
               Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
               if (roles.contains("ROLE_ADMIN")) {  // 권한이 관리자이면
                  response.sendRedirect("/home");  // 관리자페이지로 넘기기
               } else {  // 사용자라면
                  response.sendRedirect("/home");  // /home페이지로 넘기기
               }
            })
            .usernameParameter("email")
            .failureUrl("/members/login/error") // 실패시 이동 url /members/login/error
      ).logout(logout -> logout // 로그아웃 버튼을 누르면
            .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃 실행
            .logoutSuccessUrl("/") // 로그아웃 성공시 실행
            .invalidateHttpSession(true) // 세션 무효화
            .deleteCookies("JSESSIONID") // 쿠키 삭제
      ).oauth2Login(oauthLogin -> oauthLogin
            .defaultSuccessUrl("/home")
            .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                  .userService(customOAuth2UserService))
      );
      
      
      http.exceptionHandling(exception -> exception
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
      
      return http.build();
   }
   
   @Bean // 패스워드 암호화 해주는 객체
   public static PasswordEncoder passwordEncoder() {
      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
   }
   
   @Autowired
   // AuthenticationManagerBuilder -> UserDetailService를 구현하고 그 객체 MemberService 지정과
   // 동시에 비밀번호를 암호화
   public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
   }
}
