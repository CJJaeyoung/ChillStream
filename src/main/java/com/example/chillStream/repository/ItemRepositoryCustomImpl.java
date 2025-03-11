package com.example.chillStream.repository;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import com.example.chillStream.dto.ItemCrawlSearchDto;
import com.example.chillStream.dto.ItemSearchDto;
import com.example.chillStream.entity.Item;
import com.example.chillStream.entity.ItemCrawl;
import com.example.chillStream.entity.QItem;
import com.example.chillStream.entity.QItemCrawl;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;
    
    @PersistenceContext
    private EntityManager entityManager;

    public ItemRepositoryCustomImpl(EntityManager entityManager){
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    private BooleanExpression searchMainGenreEq(MainGenre searchMainGenre){
        return searchMainGenre == null
                ? null
                : QItem.item.mainGenre.eq(searchMainGenre);
    }

    private BooleanExpression searchSubGenreEq(SubGenre searchSubGenre){
        return searchSubGenre == null
                ? null
                : QItem.item.subGenre.eq(searchSubGenre);
    }

    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all",searchDateType) || searchDateType == null){
            return null;
        }
        else if(StringUtils.equals("1d",searchDateType)){
            dateTime = dateTime.minusDays(1);
        }
        else if(StringUtils.equals("1W",searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }
        else if(StringUtils.equals("1m",searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }
        else if(StringUtils.equals("6m",searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("title", searchBy)){
            return QItem.item.title.like("%"+searchQuery+"%");
        }
        else if(StringUtils.equals("createdBy",searchBy)){
            return QItem.item.createdBy.like("%"+searchQuery+"%");
        }
        else {
            return null;
        }
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results =queryFactory.selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType())
                        ,searchMainGenreEq(itemSearchDto.getSearchMainGenre())
                        ,searchSubGenreEq(itemSearchDto.getSearchSubGenre())
                        ,searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
    
    
    
    public List<Item> searchItems(ItemSearchDto itemSearchDto) {
        // QueryDSL로 검색 로직 구현
        return queryFactory.selectFrom(QItem.item)
              .where(
                    searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()),
                    filterByMainGenre(itemSearchDto.getSearchMainGenre()),
                    filterBySubGenre(itemSearchDto.getSearchSubGenre())
              )
              .orderBy(QItem.item.id.desc()) // 정렬 조건
              .fetch(); // 결과 가져오기
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
