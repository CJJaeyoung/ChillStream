package com.example.chillStream.controller;

import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.dto.CommentDto;
import com.example.chillStream.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    private final static int PAGE_SIZE = 5;
    
    @PostMapping("/new") //댓글 등록
    public @ResponseBody ResponseEntity saveComment(@RequestBody CommentDto commentDto, Principal principal) {
        String email = SecurityUtil.getCurrentUserEmail();
        try {
            if (principal == null) {
                return new ResponseEntity("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            
            CommentDto savedComment = commentService.saveComment(commentDto.getContent(),
                  email,
                  commentDto.getItemId());
            return new ResponseEntity(savedComment, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        try {
            String email = SecurityUtil.getCurrentUserEmail();
            commentService.deleteComment(commentId, email);
            return new ResponseEntity<>("댓글이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/list/{itemId}") //댓글 불러오기
    public @ResponseBody ResponseEntity getComments(
          @PathVariable("itemId") Long itemId,
          @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            List<CommentDto> comments = commentService.getCommentsByItemId(itemId, page, PAGE_SIZE);
            return new ResponseEntity(comments, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글을 불러오는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
