package com.example.chillStream.repository;


import com.example.chillStream.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByIdAndMemberId(Long commentId, Long memberId);
    Page<Comment> findByItemId(Long itemId, Pageable pageable);
}
