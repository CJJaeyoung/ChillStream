package com.example.chillStream.repository;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import com.example.chillStream.dto.ItemSearchDto;
import com.example.chillStream.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
   List<Item> findByMainGenre(MainGenre mainGenre);
   
   List<Item> findBySubGenre(SubGenre subGenre);
   
   @Query(value = "SELECT * FROM item ORDER BY RAND() LIMIT :count", nativeQuery = true)
   List<Item> findRandomItems(@Param("count") int count);
   
   boolean existsByTitle(String title);
   
   List<Item> findTop5ByOrderByIdDesc();
   
   List<Item> findTop10ByOrderByIdDesc();
   
   @Query("SELECT i FROM Item i WHERE i.title LIKE %:#{#dto.searchQuery}%")
   List<Item> searchItems(@Param("dto") ItemSearchDto dto);
   
}
