package com.example.chillStream.repository;

import com.example.chillStream.dto.VideoSearchDto;
import com.example.chillStream.entity.QVideo;
import com.example.chillStream.entity.Video;
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

public class VideoRepositoryCustomImpl implements VideoRepositoryCustom {
    private JPAQueryFactory queryFactory; // 동적쿼리 사용하기 위해 JPAQueryFactory 변수 선언
    // 생성자

    public VideoRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em); // JPAQueryFactory 실질적인 객체가 만들어 집니다.
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1W", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QVideo.video.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("episodeNumber", searchBy)){
            return QVideo.video.episodeNumber.like("%" + searchQuery + "%");
        }else if (StringUtils.equals("title", searchBy)) {
            return QVideo.video.title.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QVideo.video.createdBy.like("%" + searchQuery + "%");
        } else {
            return null;
        }
    }

    private BooleanExpression getvideosByItemId(Long itemId){
        return QVideo.video.item.id.eq(itemId);
    }

    @Override
    public Page<Video> getAdminVideoPage(Long itemId, VideoSearchDto videoSearchDto, Pageable pageable) {
        QueryResults<Video> results = queryFactory.selectFrom(QVideo.video)
                .where(getvideosByItemId(itemId),
                        regDtsAfter(videoSearchDto.getSearchDateType()),
                        searchByLike(videoSearchDto.getSearchBy(), videoSearchDto.getSearchQuery()))
                .orderBy(QVideo.video.episodeNumber.desc())
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();

        List<Video> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}
