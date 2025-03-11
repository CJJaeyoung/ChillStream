package com.example.chillStream.controller;

import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.dto.AdDto;
import com.example.chillStream.dto.CommentCrawlDto;
import com.example.chillStream.dto.ItemCrawlDto;
import com.example.chillStream.dto.MemberDto;
import com.example.chillStream.entity.Member;
import com.example.chillStream.service.ItemCrawlService;
import com.example.chillStream.service.MemberService;
import com.example.chillStream.service.RecommendationService;
import com.example.chillStream.service.VideoService;
import com.example.chillStream.util.YoutubeUrlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/crawled")
public class CrawlItemController {
    
    private final ItemCrawlService itemCrawlService;
    private final RecommendationService recommendationService;
    private final MemberService memberService;
    private final VideoService videoService;
    private final static int PAGE_SIZE = 5;
    
    //크롤링 비디오 재생 페이지
    @GetMapping("/video/playCrawl")
    public String goToCrawl(Model model){
        String email = SecurityUtil.getCurrentUserEmail();
        try {
            MemberDto memberDto = videoService.getMemberDto(email);
            AdDto adDto = videoService.getAd();
            
            model.addAttribute("memberDto", memberDto);
            model.addAttribute("adDto", adDto);
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "동영상을 재생할 수 없습니다.");
            return "error/404";
        }
        return "video/playCrawl";
    }
    
    //크롤링 상세 페이지
    @GetMapping(value = "/{crawlItemId}")
    public String getCrawlItemDtl(Model model, @PathVariable("crawlItemId") Integer crawlItemId) {
        try {
            ItemCrawlDto itemCrawlDto = itemCrawlService.getCrawlItemDtl(crawlItemId);
            String email = SecurityUtil.getCurrentUserEmail();
            Member member = memberService.findMemberByEmail(email);
            List<ItemCrawlDto> recommendedContents = recommendationService.getFive(member.getId());
            // YouTube URL을 임베드 URL로 변환
            if (itemCrawlDto.getTrailerUrl() != null) {
                String embedUrl = YoutubeUrlConverter.convertToEmbedUrl(itemCrawlDto.getTrailerUrl());
                model.addAttribute("embedUrl", embedUrl);
            }
            model.addAttribute("memberId",member.getId());
            model.addAttribute("email", email);
            model.addAttribute("itemCrawlDto", itemCrawlDto);
            model.addAttribute("recommendedContents", recommendedContents);
            
        } catch(EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "item/crawledItem";
        }
        return "item/crawledItem";
    }
    
    //댓글 등록
    @PostMapping("/comment/new")
    public @ResponseBody ResponseEntity saveComment(@RequestBody CommentCrawlDto commentCrawlDto, Principal principal) {
        String email = SecurityUtil.getCurrentUserEmail();
        try {
            if (principal == null) {
                return new ResponseEntity("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            
            CommentCrawlDto savedComment = itemCrawlService.saveComment(commentCrawlDto.getContent(),
                  email,
                  commentCrawlDto.getTmdbId());
            return new ResponseEntity(savedComment, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
    }
    
    //댓글 삭제
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        try {
            String email = SecurityUtil.getCurrentUserEmail();
            itemCrawlService.deleteComment(commentId, email);
            return new ResponseEntity<>("댓글이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    //댓글 불러오기
    @GetMapping("/comment/list/{tmdbId}")
    public @ResponseBody ResponseEntity getComments(
          @PathVariable("tmdbId") Integer tmdbId,
          @RequestParam(value = "page", defaultValue = "0") int page) {
        try {
            List<CommentCrawlDto> comments = itemCrawlService.getCommentsByItemId(tmdbId, page, PAGE_SIZE);
            return new ResponseEntity(comments, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("댓글을 불러오는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
