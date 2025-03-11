package com.example.chillStream.service;

import com.example.chillStream.dto.CommentDto;
import com.example.chillStream.entity.Comment;
import com.example.chillStream.entity.Item;
import com.example.chillStream.entity.Member;
import com.example.chillStream.repository.CommentRepository;
import com.example.chillStream.repository.ItemRepository;
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
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //1.댓글을 저장하는 기능
    public CommentDto saveComment(String content, String email, Long itemId) {
        // 입력값 검증
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("영상 ID가 필요합니다.");
        }

        // 엔티티 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("영상을 찾을 수 없습니다."));
        
        // 댓글 생성 및 저장
        Comment comment = new Comment();
        comment.setMember(member);
        comment.setItem(item);
        comment.setContent(content.trim());
        comment.setLike(0);

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    //2.댓글을 불러오는 기능(최신 순)
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByItemId(Long itemId, int page, int size) {
        if (itemId == null) {
            throw new IllegalArgumentException("영상 ID가 필요합니다.");
        }
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regTime"));
        Page<Comment> comments = commentRepository.findByItemId(itemId, pageRequest);

        return comments.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    //dto -> entity로 변환
    private CommentDto convertToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .memberEmail(comment.getMember().getEmail()) // member를 다시 조회하지 않고 바로 사용
                .regTime(comment.getRegTime())
                .build();
    }

    //3.댓글을 불러오는 기능(좋아요 순)
    //4.작성자가 댓글을 수정하는 기능 (수정됨)표시

    //5.작성자가 댓글을 삭제하는 기능 (관리자 삭제 기능 추후 추가)
    public void deleteComment(Long commentId, String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        Comment comment = commentRepository.findByIdAndMemberId(commentId, member.getId());
        commentRepository.delete(comment);
    }
    //6.댓글을 신고하는 기능
}
