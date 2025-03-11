package com.example.chillStream.repository;

import com.example.chillStream.entity.Comment;
import com.example.chillStream.entity.CommentCrawl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentCrawlRepository extends JpaRepository<CommentCrawl, Long> {
    CommentCrawl findByIdAndMemberId(Long commentCrawlId, Long memberId);
    Page<CommentCrawl> findByItemCrawlId(Long itemCrawlId, Pageable pageable);
}
