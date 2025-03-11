package com.example.chillStream.service;

import com.example.chillStream.dto.*;
import com.example.chillStream.entity.*;
import com.example.chillStream.repository.CommentCrawlRepository;
import com.example.chillStream.repository.ItemCrawlRepository;
import com.example.chillStream.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemCrawlService {
    
    private final ItemCrawlRepository itemCrawlRepository;
    private final MemberRepository memberRepository;
    private final CommentCrawlRepository commentCrawlRepository;
    
    //상세 페이지 불러오는 메서드
    @Transactional(readOnly = true)
    public ItemCrawlDto getCrawlItemDtl(Integer itemCrawlId) {
        ItemCrawl itemCrawl = itemCrawlRepository.findByTmdbId(itemCrawlId).orElseThrow(EntityNotFoundException::new);
        return new ItemCrawlDto(itemCrawl);
    }
    
    //1.댓글을 저장하는 기능
    public CommentCrawlDto saveComment(String content, String email, Integer tmdbId) {
        //입력값 검증
        if(content == null || content.trim().isEmpty()){
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
        if(tmdbId == null){
            throw new IllegalArgumentException("영상 ID가 필요합니다.");
        }
        
        //엔티티 조회
        Member member = memberRepository.findByEmail(email)
              .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        ItemCrawl itemCrawl = itemCrawlRepository.findByTmdbId(tmdbId)
              .orElseThrow(() -> new EntityNotFoundException("영상을 찾을 수 없습니다."));
        
        //댓글 생성 및 저장
        CommentCrawl commentCrawl = new CommentCrawl();
        commentCrawl.setMember(member);
        commentCrawl.setItemCrawl(itemCrawl);
        commentCrawl.setContent(content.trim());
        commentCrawl.setLike(0);
        
        CommentCrawl savedCommentCrawl = commentCrawlRepository.save(commentCrawl);
        return convertToDto(commentCrawl);
    }
    
    //2.댓글을 불러오는 기능(최신 순)
    @Transactional(readOnly = true)
    public List<CommentCrawlDto> getCommentsByItemId(Integer tmdbId, int page, int size) {
        if (tmdbId == null) {
            throw new IllegalArgumentException("영상 ID가 필요합니다.");
        }
        ItemCrawl itemCrawl = itemCrawlRepository.findByTmdbId(tmdbId).orElseThrow(EntityNotFoundException::new);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<CommentCrawl> commentCrawls = commentCrawlRepository.findByItemCrawlId(itemCrawl.getId(), pageRequest);
        
        return commentCrawls.getContent().stream()
              .map(this::convertToDto)
              .collect(Collectors.toList());
    }
    //dto -> entity로 변환
    private CommentCrawlDto convertToDto(CommentCrawl commentCrawl) {
        return CommentCrawlDto.builder()
              .id(commentCrawl.getId())
              .content(commentCrawl.getContent())
              .memberEmail(commentCrawl.getMember().getEmail()) // member를 다시 조회하지 않고 바로 사용
              .regTime(commentCrawl.getRegTime())
              .build();
    }
    
    //3.댓글을 불러오는 기능(좋아요 순)
    //4.작성자가 댓글을 수정하는 기능 (수정됨)표시
    
    //5.작성자가 댓글을 삭제하는 기능 (관리자 삭제 기능 추후 추가)
    public void deleteComment(Long commentId, String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        CommentCrawl commentCrawl = commentCrawlRepository.findByIdAndMemberId(commentId, member.getId());
        commentCrawlRepository.delete(commentCrawl);
    }
}