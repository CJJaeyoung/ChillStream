package com.example.chillStream.repository;

import com.example.chillStream.dto.AdSearchDto;
import com.example.chillStream.entity.Ad;
import com.example.chillStream.entity.QAd;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class AdRepositoryCustomImpl implements AdRepositoryCustom {
    
    private JPAQueryFactory queryFactory;
    
    public AdRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    private BooleanExpression regDtsAfter(String searchDateType) {
        if (StringUtils.isEmpty(searchDateType) || searchDateType == null) {
            return null;
        }
        
        LocalDateTime dateTime = LocalDateTime.now();
        QAd ad = QAd.ad;
        
        switch (searchDateType) {
            case "1d":
                dateTime = dateTime.minusDays(1);
                break;
            case "1w":
                dateTime = dateTime.minusWeeks(1);
                break;
            case "1m":
                dateTime = dateTime.minusMonths(1);
                break;
            case "6m":
                dateTime = dateTime.minusMonths(6);
                break;
            default:
                return null;
        }
        
        return ad.regTime.after(dateTime);
    }
    
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.isEmpty(searchBy) || StringUtils.isEmpty(searchQuery)) {
            return null;
        }
        
        QAd ad = QAd.ad;
        
        switch (searchBy) {
            case "title":
                return ad.title.like("%" + searchQuery + "%");
            case "createdBy":
                return ad.createdBy.like("%" + searchQuery + "%");
            default:
                return null;
        }
    }
    
    @Override
    public Page<Ad> getAdminAdPage(AdSearchDto adSearchDto, Pageable pageable) {
        QAd ad = QAd.ad;
        
        List<Ad> content = queryFactory
              .selectFrom(ad)
              .where(
                    regDtsAfter(adSearchDto.getSearchDateType()),
                    searchByLike(adSearchDto.getSearchBy(), adSearchDto.getSearchQuery()))
              .orderBy(ad.id.desc())
              .offset(pageable.getOffset())
              .limit(pageable.getPageSize())
              .fetch();
        
        long total = queryFactory
              .select(ad.count())
              .from(ad)
              .where(
                    regDtsAfter(adSearchDto.getSearchDateType()),
                    searchByLike(adSearchDto.getSearchBy(), adSearchDto.getSearchQuery()))
              .fetchOne();
        
        return new PageImpl<>(content, pageable, total);
    }
}
