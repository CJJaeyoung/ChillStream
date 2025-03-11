package com.example.chillStream.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_movie_selection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMovieSelection {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "member_id", nullable = false) // 회원 ID 외래키 설정
   private Member member; // 사용자 정보와의 관계 설정

   @Column(nullable = false)
   private Long movieId; // 영화 ID

   @Column(nullable = false, length = 50)
   private String mainGenre; // 메인 장르

   @Column(length = 50)
   private String subGenre; // 서브 장르

   @Column(nullable = false)
   private Integer priority; // 선택 우선순위
   private Integer step;



}