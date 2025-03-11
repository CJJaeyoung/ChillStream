package com.example.chillStream.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import com.example.chillStream.repository.UserMovieSelectionRepository;
import com.example.chillStream.entity.ItemCrawl;
import com.example.chillStream.repository.ItemCrawlRepository;
import com.example.chillStream.dto.ThumbnailDto;
import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.dto.ItemCrawlDto;

@Service
@RequiredArgsConstructor
public class RecommendationService {
   private final UserMovieSelectionRepository userMovieSelectionRepository;
   private final ItemCrawlRepository itemCrawlRepository;
   
   public List<ItemCrawlDto> getRecommendedContents(Long memberId) {
      // 사용자의 장르 선호도 가져오기
      List<Object[]> genrePreferences = userMovieSelectionRepository.findGenrePreferencesByMemberId(memberId);
      
      // 장르별 가중치 계산
      Map<MainGenre, Integer> genreWeights = new HashMap<>();
      for (Object[] preference : genrePreferences) {
         String genreStr = (String) preference[0]; // `preference[0]`을 String으로 캐스팅
         MainGenre genre = MainGenre.valueOf(genreStr);
         Integer weight = ((Number) preference[1]).intValue();
         genreWeights.put(genre, weight);
      }
      
      // 추천 콘텐츠 조회 (상위 10개)
      List<ItemCrawl> recommendedItems = itemCrawlRepository.findAll().stream()
            .sorted((i1, i2) -> {
               int weight1 = genreWeights.getOrDefault(i1.getMainGenre(), 0);
               int weight2 = genreWeights.getOrDefault(i2.getMainGenre(), 0);
               return weight2 - weight1;
            })
            .limit(10)
            .collect(Collectors.toList());
      
      // ItemFormDto로 변환
      return recommendedItems.stream()
            .map(item -> {
               ItemCrawlDto dto = new ItemCrawlDto();
               dto.setTitle(item.getTitle());
               dto.setMainGenre(item.getMainGenre());
               dto.setSubGenre(item.getSubGenre());
               dto.setVoteAverage(item.getVoteAverage());
               dto.setTmdbId(item.getTmdbId());
               
               ThumbnailDto thumbnailDto = new ThumbnailDto();
               thumbnailDto.setThumbnailName(item.getThumbnailUrl());
               dto.setThumbnailDto(thumbnailDto);
               
               return dto;
            })
            .collect(Collectors.toList());
   }
   
   public List<ItemCrawlDto> getFive(Long memberId) {
      List<ItemCrawlDto> recommendedContents = getRecommendedContents(memberId); //10개 짜리 리스트
      List<ItemCrawlDto> five = new ArrayList<>(); //5개짜리 리스트
      Random r = new Random();
      int[] arr = new int[5];
      for (int i = 0; i < 5; i++) {
         arr[i] = r.nextInt(10); //0~9까지 숫자 랜덤
         five.add(recommendedContents.get(arr[i])); //0~9번째 객체 중 랜덤으로 5개 삽입
         for (int j = 0; j < i; j++) {
            if (arr[i] == arr[j]) { //중복 체크
               five.remove(i); //삭제
               i--;
               break;
            }
         }
      }
      return five;
   }
}