package com.example.chillStream.controller;

import com.example.chillStream.dto.ItemFormDto;
import com.example.chillStream.entity.Member;
import com.example.chillStream.service.MemberService;
import com.example.chillStream.service.VisitorCountService;
import com.example.chillStream.service.TMDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final MemberService memberService;
    private final VisitorCountService visitorCountService;
    private final TMDBService tmdbService;

    @GetMapping(value = "/") // get요청으로 /로 들어간다.
    public String main(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails -> 현재 인증된 사용자 정보를 나타낸다.
        if (userDetails != null) {  // 만약 사용자가 null이 아니라면,
            Member member = memberService.findMemberByEmail(userDetails.getUsername());
            // 인증된 사용자의 이메일로 회원정보를 조회한다.
            model.addAttribute("mainDto", member);
            // 조회된 회원 정보를 "mainDto"라는 이름으로 뷰에 전달한다.
        }
        visitorCountService.incrementVisitorCount();  // 방문자수 1 증가시키는 서비스 호출 (채팅이랑 다른 내용)
        model.addAttribute("todayCount", visitorCountService.getTodayVisitorCount());
        model.addAttribute("totalCount", visitorCountService.getTotalVisitorCount());
        model.addAttribute("top10Trending", tmdbService.getTop10Trending());

        return "main";
    }
}
