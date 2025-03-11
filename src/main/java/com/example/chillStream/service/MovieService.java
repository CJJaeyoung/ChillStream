package com.example.chillStream.service;

import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.dto.ItemCrawlDto;
import com.example.chillStream.dto.ItemCrawlSearchDto;
import com.example.chillStream.dto.MovieSelectionRequestDto;
import com.example.chillStream.entity.ItemCrawl;
import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.UserMovieSelection;
import com.example.chillStream.repository.ItemCrawlRepository;
import com.example.chillStream.repository.MemberRepository;
import com.example.chillStream.repository.UserMovieSelectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MovieService {

    private final ItemCrawlRepository itemCrawlRepository;
    private final UserMovieSelectionRepository userMovieSelectionRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;


    // 랜덤으로 데이터 가져오기
    public List<ItemCrawl> getRandomItems(int count) {
        List<ItemCrawl> allItems = itemCrawlRepository.findAll(); // 모든 아이템 가져오기
        Collections.shuffle(allItems); // 무작위로 섞기
        return allItems.stream().limit(count).toList(); // 15개 반환
    }


    @Transactional
    public void saveUserSelect(List<MovieSelectionRequestDto> selections) {
        // 현재 로그인한 사용자의 이메일 가져오기
        String email = SecurityUtil.getCurrentUserEmail();

        // 이메일로 memberId 조회
        Long memberId = memberService.findMemberIdByEmail(email);

        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새로운 선택 저장
        for (MovieSelectionRequestDto selection : selections) {
            UserMovieSelection userMovieSelection = new UserMovieSelection();
            userMovieSelection.setMember(member); // 로그인한 사용자와 연결
            userMovieSelection.setMovieId(selection.getId());
            userMovieSelection.setMainGenre(selection.getMainGenre());
            userMovieSelection.setSubGenre(selection.getSubGenre());
            userMovieSelection.setPriority(selection.getPriority());
            userMovieSelectionRepository.save(userMovieSelection);
        }
    }
    
    
    public List<ItemCrawlDto> searchMovies(String searchQuery) {
        // 검색 로직 추가
        ItemCrawlSearchDto searchDto = new ItemCrawlSearchDto();
        searchDto.setSearchBy("title");
        searchDto.setSearchQuery(searchQuery);
        
        return itemCrawlRepository.searchItemCrawls(searchDto)
              .stream()
              .map(item -> new ItemCrawlDto(item)) // Item -> ItemFormDto 변환
              .toList();
    }
    
}
