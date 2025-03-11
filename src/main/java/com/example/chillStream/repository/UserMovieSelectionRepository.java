package com.example.chillStream.repository;

import com.example.chillStream.entity.UserMovieSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserMovieSelectionRepository extends JpaRepository<UserMovieSelection, Long> {
   
   List<UserMovieSelection> findByMemberIdOrderByPriorityAsc(Long memberId);
   
   @Query("SELECT ums.mainGenre, SUM(CASE ums.priority " +
         "WHEN 1 THEN 3 " +
         "WHEN 2 THEN 2 " +
         "WHEN 3 THEN 1 END) " +
         "FROM UserMovieSelection ums " +
         "WHERE ums.member.id = :memberId " +
         "GROUP BY ums.mainGenre")
   List<Object[]> findGenrePreferencesByMemberId(@Param("memberId") Long memberId);
   
}