package com.example.chillStream.repository;


import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import com.example.chillStream.dto.ItemCrawlSearchDto;
import com.example.chillStream.entity.ItemCrawl;
import com.example.chillStream.entity.QItemCrawl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ItemCrawlRepositoryImpl implements ItemCrawlRepositoryCustom {
   private JPAQueryFactory queryFactory; // 동적쿼리 사용하기 위해 JPAQueryFactory 변수 선언
   // 생성자
   
   public ItemCrawlRepositoryImpl(EntityManager em){
      this.queryFactory = new JPAQueryFactory(em); // JPAQueryFactory 실질적인 객체가 만들어 집니다.
   }
   
   @Override
   public List<ItemCrawl> searchItemCrawls(ItemCrawlSearchDto itemCrawlSearchDto) {
      // QueryDSL로 검색 로직 구현
      return queryFactory.selectFrom(QItemCrawl.itemCrawl)
            .where(
                  searchByLike(itemCrawlSearchDto.getSearchBy(), itemCrawlSearchDto.getSearchQuery()),
                  filterByMainGenre(itemCrawlSearchDto.getMainGenre()),
                  filterBySubGenre(itemCrawlSearchDto.getSubGenre())
            )
            .orderBy(QItemCrawl.itemCrawl.id.desc()) // 정렬 조건
            .fetch(); // 결과 가져오기
   }
   
   // 검색 조건 메서드
   private BooleanExpression searchByLike(String searchBy, String searchQuery) {
      if (searchQuery == null || searchQuery.isEmpty()) {
         return null; // 검색어가 없으면 조건 제외
      }
      if ("title".equals(searchBy)) {
         return QItemCrawl.itemCrawl.title.like("%" + searchQuery + "%");
      } else if ("itemDetail".equals(searchBy)) {
         return QItemCrawl.itemCrawl.itemDetail.like("%" + searchQuery + "%");
      }
      return null;
   }
   
   // 메인 장르 필터링
   private BooleanExpression filterByMainGenre(MainGenre mainGenre) {
      if (mainGenre == null) {
         return null; // 메인 장르가 없으면 조건 제외
      }
      return QItemCrawl.itemCrawl.mainGenre.eq(mainGenre);
   }
   
   // 서브 장르 필터링
   private BooleanExpression filterBySubGenre(SubGenre subGenre) {
      if (subGenre == null) {
         return null; // 서브 장르가 없으면 조건 제외
      }
      return QItemCrawl.itemCrawl.subGenre.eq(subGenre);
   }
}

