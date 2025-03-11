package com.example.chillStream.controller;

import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.dto.*;
import com.example.chillStream.entity.Member;
import com.example.chillStream.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class HomeController {
   private final MemberService memberService;
   private final TMDBService tmdbService;
   private final RecommendationService recommendationService;
   private final MovieService movieService;
   private final ItemService itemService;
   private final BannerService bannerService;
   
   
   private Member getLoggedInMember(Principal principal) {
      String email = SecurityUtil.getCurrentUserEmail();
      return memberService.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
   }
   
   @GetMapping("/home")
   public String home(HttpServletRequest request, Model model, Principal principal, HttpServletResponse response,
                      @RequestParam(required = false) String searchQuery) throws IOException {
      if (principal == null) {
         return "redirect:/members/login";
      }
      try {
         Member member = getLoggedInMember(principal);
         model.addAttribute("memberId", member.getId());
         // isFirstLogin 상태를 모델에 추가
         String currentPath = request.getRequestURI();
         model.addAttribute("currentPath", currentPath);
         model.addAttribute("isFirstLogin", member.isFirstLogin());
         
        
         
         // 캐러셀용 최신 콘텐츠 (3개로 제한)
         List<BannnerFormDto> latestContents = bannerService.getLatestContents().stream()
               .limit(3)
               .toList();
         model.addAttribute("latestContents", latestContents);
         
         // 전체 최신 콘텐츠
         List<ItemFormDto> items = itemService.getAllItemDtos();
         model.addAttribute("items", items);
         
         // 각 카테고리별 TOP 10
         model.addAttribute("top10Trending", tmdbService.getTop10Trending());  // 전체 인기 TOP 10
         model.addAttribute("top10Movies", tmdbService.getTop10Movies());      // 영화 TOP 10
         model.addAttribute("top10TvShows", tmdbService.getTop10TvShows());   // 드라마 TOP 10
         model.addAttribute("top10Animation", tmdbService.getTop10Animation());  // 애니메이션 TOP 10
         model.addAttribute("top10Entertainment", tmdbService.getTop10Entertainment());  // 예능 TOP 10
         List<ItemCrawlDto> allContents = tmdbService.getAllCrawledItems();
         model.addAttribute("allContents", allContents);
         // 추천 콘텐츠 추가
         List<ItemCrawlDto> recommendedContents =
               recommendationService.getRecommendedContents(member.getId());
         model.addAttribute("recommendedContents", recommendedContents);
         
         if (searchQuery != null && !searchQuery.isEmpty()) {
            System.out.println("검색어: " + searchQuery);
            ItemCrawlSearchDto searchDto = new ItemCrawlSearchDto();
            searchDto.setSearchQuery(searchQuery);
            searchDto.setSearchBy("title");
            List<ItemCrawlDto> searchResults = itemService.searchItemCrawls(searchDto);
            System.out.println("검색 결과: " + searchResults);
            
            ItemSearchDto itemSearchDto = new ItemSearchDto();
            itemSearchDto.setSearchQuery(searchQuery);
            itemSearchDto.setSearchBy("title");
            List<ItemFormDto> itemSearchResults = itemService.searchItems(itemSearchDto);
            System.out.println("검색결과2: " + itemSearchResults);
            
            
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("itemSearchResults", itemSearchResults);
            
         } else {
            
            model.addAttribute("searchQuery", null);
            model.addAttribute("searchResults", null);
            model.addAttribute("itemSearchResults", null);
            
         }
         
         return "home/home";
      } catch (UsernameNotFoundException e) {
         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + e.getMessage());
         return null;
      }
   }
   
   @PostMapping("/home/first-login-complete")
   public ResponseEntity<String> completeFirstLogin(Principal principal) {
      Member member = getLoggedInMember(principal);
      
      // firstLogin 상태를 false로 변경
      memberService.updateFirstLogin(member.getId(), false);
      
      return ResponseEntity.ok("First login completed successfully.");
   }
}
