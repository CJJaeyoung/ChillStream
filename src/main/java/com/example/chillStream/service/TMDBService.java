package com.example.chillStream.service;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.constant.SubGenre;
import com.example.chillStream.dto.ItemCrawlDto;
import com.example.chillStream.dto.ThumbnailDto;
import com.example.chillStream.entity.ItemCrawl;
import com.example.chillStream.repository.ItemCrawlRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TMDBService {
   
   private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
   private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
   private static final int ITEMS_PER_GENRE = 25;
   private static final int MAX_OVERVIEW_LENGTH = 500;
   private static final int MAX_ITEMS = 10;  // 최대 아이템 개수 제한
   
   // TMDB 장르 ID와 SubGenre 매핑 확장
   private static final Map<Integer, SubGenre> GENRE_MAPPING = new HashMap<>() {{
      put(14, SubGenre.FANTASY);      // Fantasy
      put(878, SubGenre.SF);          // Science Fiction
      put(18, SubGenre.DRAMA);        // Drama
      put(10749, SubGenre.ROMANCE);   // Romance
      put(35, SubGenre.COMEDY);       // Comedy
      put(28, SubGenre.ACTION);       // Action
      put(53, SubGenre.THRILLER);     // Thriller
      put(36, SubGenre.HISTORY);      // History
      put(27, SubGenre.HORROR);       // Horror
      put(9648, SubGenre.MYSTERY);    // Mystery
      put(80, SubGenre.CRIME);        // Crime
      put(12, SubGenre.ADVENTURE);    // Adventure
      put(10751, SubGenre.FAMILY);    // Family
      put(10752, SubGenre.WAR);       // War
      put(37, SubGenre.WESTERN);      // Western
      put(99, SubGenre.DOCUMENTARY);  // Documentary
      put(10402, SubGenre.MUSIC);     // Music
      put(16, SubGenre.ANIMATION);    // Animation
      put(10764, SubGenre.VARIETY);   // Reality/Variety
   }};

   //국가 코드를 한글로 매핑
   private static final Map<String, String> COUNTRY_NAME_MAPPING = new HashMap<>() {{
      put("US", "미국");
      put("KR", "대한민국");
      put("JP", "일본");
      put("CN", "중국");
      put("FR", "프랑스");
      put("DE", "독일");
      put("IN", "인도");
      put("GB", "영국");
      put("IT", "이탈리아");
      put("ES", "스페인");
      put("CA", "캐나다");
      put("NZ", "뉴질랜드");
      put("IE", "아일랜드");
      put("HK", "홍콩");
      put("PL", "폴란드");
      put("BE", "벨기에");
      put("CO", "콜롬비아");
      // 필요에 따라 추가...
   }};

   //국가를 한글로 변환
   private String convertCountriesToKorean(JsonNode productionCountries) {
      List<String> countries = new ArrayList<>();
      for (JsonNode country : productionCountries) {
         String countryCode = country.get("iso_3166_1").asText();
         String countryName = COUNTRY_NAME_MAPPING.getOrDefault(countryCode, country.get("name").asText());
         countries.add(countryName);
      }
      return String.join(", ", countries);
   }
   
   @Value("${tmdb.api.key}")
   private String apiKey;
   
   private final RestTemplate restTemplate;
   private final ItemCrawlRepository itemCrawlRepository;
   private final ObjectMapper objectMapper;
   
   private String truncateOverview(String overview) {
      if (overview == null || overview.isEmpty()) {
         return "상세 설명이 없습니다.";
      }
      
      if (overview.length() > MAX_OVERVIEW_LENGTH) {
         int lastPeriod = overview.substring(0, MAX_OVERVIEW_LENGTH).lastIndexOf('.');
         if (lastPeriod > 0) {
            return overview.substring(0, lastPeriod + 1);
         }
         return overview.substring(0, MAX_OVERVIEW_LENGTH) + "...";
      }
      return overview;
   }
   
   private SubGenre determineSubGenre(JsonNode genreIds, MainGenre mainGenre) {
      // 예능인 경우 VARIETY로 고정
      if (mainGenre == MainGenre.ENTERTAINMENT) {
         return SubGenre.VARIETY;
      }
      
      // 애니메이션인 경우 ANIMATION으로 고정
      if (mainGenre == MainGenre.ANIMATION) {
         return SubGenre.ANIMATION;
      }
      
      // 장르 ID 배열에서 매핑된 첫 번째 서브장르 반환
      if (genreIds != null && genreIds.isArray()) {
         for (JsonNode genreId : genreIds) {
            SubGenre subGenre = GENRE_MAPPING.get(genreId.asInt());
            if (subGenre != null) {
               return subGenre;
            }
         }
      }
      
      // 매핑된 장르가 없는 경우 ETC 반환
      return SubGenre.ETC;
   }
   
   private void processResults(JsonNode results, MainGenre mainGenre) {
      int savedCount = 0;
      int index = 0;
      
      while (savedCount < ITEMS_PER_GENRE && index < results.size()) {
         JsonNode result = results.get(index);
         
         try {
            JsonNode titleNode = result.has("name") ? result.get("name") : result.get("title");
            Integer tmdbId = result.get("id").asInt();
            
            // 상세 정보 API 호출
            String detailUrl = String.format("%s/%s/%d?api_key=%s&language=ko-KR&append_to_response=videos,credits",
                  TMDB_BASE_URL,
                  mainGenre == MainGenre.MOVIE ? "movie" : "tv",
                  tmdbId,
                  apiKey);
            
            ResponseEntity<String> detailResponse = restTemplate.getForEntity(detailUrl, String.class);
            JsonNode detailData = objectMapper.readTree(detailResponse.getBody());
            
            // 평점과 인기도 데이터
            double voteAverage = detailData.has("vote_average") ? detailData.get("vote_average").asDouble() : 0.0;
            double popularity = detailData.has("popularity") ? detailData.get("popularity").asDouble() : 0.0;
            
            // 한국어 제목과 설명
            String koreanTitle = detailData.has("title") ? detailData.get("title").asText() :
                  (detailData.has("name") ? detailData.get("name").asText() : titleNode.asText());
            String koreanOverview = detailData.has("overview") ? detailData.get("overview").asText() : "";
            
            // 장르 ID 배열 가져오기
            JsonNode genreIds = result.get("genre_ids");
            SubGenre subGenre = determineSubGenre(genreIds, mainGenre);
            
            // 비디오 정보 가져오기
            String videoUrl = String.format("%s/%s/%d/videos?api_key=%s&language=ko-KR",
                  TMDB_BASE_URL,
                  mainGenre == MainGenre.MOVIE ? "movie" : "tv",
                  tmdbId,
                  apiKey);
            
            ResponseEntity<String> videoResponse = restTemplate.getForEntity(videoUrl, String.class);
            JsonNode videoData = objectMapper.readTree(videoResponse.getBody());
            
            // 트레일러 URL 찾기
            String trailerUrl = null;
            if (videoData.has("results") && videoData.get("results").isArray()) {
               JsonNode videos = videoData.get("results");
               for (JsonNode video : videos) {
                  String type = video.get("type").asText();
                  String site = video.get("site").asText();
                  if ("Trailer".equals(type) && "YouTube".equals(site)) {
                     trailerUrl = "https://www.youtube.com/watch?v=" + video.get("key").asText();
                     break;
                  }
               }
            }

            // 제작국가 정보 가져오기
            String countryString = "";
            if (detailData.has("production_countries")) {
               JsonNode productionCountries = detailData.get("production_countries");
               countryString = convertCountriesToKorean(productionCountries); // 한글로 변환
            }
            
            
            // 이미 존재하는 데이터 건너뛰기
            if (!itemCrawlRepository.existsByTmdbId(tmdbId)) {
               ItemCrawlDto itemCrawlDto = ItemCrawlDto.builder()
                     .title(koreanTitle)
                     .itemDetail(truncateOverview(koreanOverview))
                     .mainGenre(mainGenre)
                     .subGenre(subGenre)
                     .runningTime(detailData.has("runtime") ? detailData.get("runtime").asInt() : null)
                     .thumbnailUrl(result.get("poster_path") != null ? IMAGE_BASE_URL + result.get("poster_path").asText() : null)
                     .releaseDate(result.has("first_air_date") ?
                           result.get("first_air_date").asText() : result.get("release_date").asText())
                     .tmdbId(tmdbId)
                     .voteAverage(voteAverage)
                     .popularity(popularity)
                     .trailerUrl(trailerUrl)
                     .productionCountries(countryString)  // 제작국가 정보 추가
                     .build();
               
               itemCrawlRepository.save(itemCrawlDto.toEntity());
               log.info("Saved content: {} (Rating: {}, Popularity: {}, Countries: {})",
                     koreanTitle, voteAverage, popularity, countryString);
               savedCount++;
            }
         } catch (Exception e) {
            log.error("Error processing item: {}", e.getMessage());
         }
         
         index++;
      }
      
      log.info("Saved {} items for genre {}", savedCount, mainGenre);
   }

   public void fetchMovies() {
      String url = String.format("%s/discover/movie?api_key=%s&with_watch_providers=8&watch_region=KR&sort_by=popularity.desc&page=1&per_page=20&language=ko-KR",
            TMDB_BASE_URL, apiKey);
      
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         processResults(root.get("results"), MainGenre.MOVIE);
      } catch (Exception e) {
         log.error("Error fetching movies: {}", e.getMessage());
      }
   }
   
   public void fetchDrama() {
      String url = String.format("%s/discover/tv?api_key=%s&with_genres=18&with_watch_providers=8&watch_region=KR&sort_by=popularity.desc&page=1&per_page=20&language=ko-KR",
            TMDB_BASE_URL, apiKey);
      
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         processResults(root.get("results"), MainGenre.DRAMA);
      } catch (Exception e) {
         log.error("Error fetching dramas: {}", e.getMessage());
      }
   }
   
   public void fetchAnimation() {
      String url = String.format("%s/discover/tv?api_key=%s&with_genres=16&with_watch_providers=8&watch_region=KR&sort_by=popularity.desc&page=1&per_page=20&language=ko-KR",
            TMDB_BASE_URL, apiKey);
      
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         processResults(root.get("results"), MainGenre.ANIMATION);
      } catch (Exception e) {
         log.error("Error fetching animations: {}", e.getMessage());
      }
   }
   
   public void fetchEntertainment() {
      String url = String.format("%s/discover/tv?api_key=%s&with_genres=10764&with_watch_providers=8&watch_region=KR&sort_by=popularity.desc&page=1&per_page=20&language=ko-KR",
            TMDB_BASE_URL, apiKey);
      
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         processResults(root.get("results"), MainGenre.ENTERTAINMENT);
      } catch (Exception e) {
         log.error("Error fetching entertainment shows: {}", e.getMessage());
      }
   }
   
   public void fetchAndSaveContent() {
      fetchMovies();
      fetchDrama();
      fetchAnimation();
      fetchEntertainment();
   }
   
   @Transactional(readOnly = true)
   public List<ItemCrawlDto> getAllCrawledItems() {
      List<ItemCrawl> crawledItems = itemCrawlRepository.findAll();
      return crawledItems.stream()
            .limit(50)
            .map(crawledItem -> {
               ItemCrawlDto itemDto = new ItemCrawlDto();
               itemDto.setTitle(crawledItem.getTitle());
               itemDto.setItemDetail(crawledItem.getItemDetail());
               itemDto.setMainGenre(crawledItem.getMainGenre());
               itemDto.setSubGenre(crawledItem.getSubGenre());
               itemDto.setVoteAverage(crawledItem.getVoteAverage());
               itemDto.setPopularity(crawledItem.getPopularity());
               itemDto.setThumbnailUrl(crawledItem.getThumbnailUrl());
               itemDto.setTmdbId(crawledItem.getTmdbId());
               itemDto.setProductionCountries(crawledItem.getProductionCountries());
               
               return itemDto;
            })
            .collect(Collectors.toList());
   }
   
   @Transactional
   public void updateCrawledDataWithTMDB() {
      List<ItemCrawl> items = itemCrawlRepository.findAll();
      int batchSize = 20;  // API 호출 제한을 고려한 배치 크기
      int updatedCount = 0;
      
      for (int i = 0; i < items.size(); i += batchSize) {
         List<ItemCrawl> batch = items.subList(i, Math.min(i + batchSize, items.size()));
         
         for (ItemCrawl item : batch) {
            try {
               // TMDB API 호출
               String detailUrl = String.format("%s/%s/%d?api_key=%s&language=ko-KR&append_to_response=videos,credits",
                     TMDB_BASE_URL,
                     item.getMainGenre() == MainGenre.MOVIE ? "movie" : "tv",
                     item.getTmdbId(),
                     apiKey);
               
               ResponseEntity<String> detailResponse = restTemplate.getForEntity(detailUrl, String.class);
               
               if (!detailResponse.getStatusCode().is2xxSuccessful()) {
                  log.warn("Failed to fetch details for item {}: {}", item.getTitle(), detailResponse.getStatusCode());
                  continue;
               }
               
               JsonNode detailData = objectMapper.readTree(detailResponse.getBody());
               
               // 데이터 업데이트 (기존 데이터 유지하면서 새로운 데이터만 업데이트)
               updateItemFromDetailData(item, detailData);
               itemCrawlRepository.save(item);
               updatedCount++;
               
               log.info("Updated item {}/{}: {} (Rating: {}, Popularity: {})",
                     updatedCount, items.size(), item.getTitle(),
                     item.getVoteAverage(), item.getPopularity());
               
               // API 호출 제한 방지를 위한 지연
               Thread.sleep(100);
               
            } catch (Exception e) {
               log.error("Error updating item {}: {}", item.getTitle(), e.getMessage());
            }
         }
         
         log.info("Processed batch {}/{}", Math.min(i + batchSize, items.size()), items.size());
      }
      
      log.info("Successfully updated {} out of {} items", updatedCount, items.size());
   }
   
   private void updateItemFromDetailData(ItemCrawl item, JsonNode detailData) {
      boolean isUpdated = false;
      
      // 제목 업데이트
      if (detailData.has("title") || detailData.has("name")) {
         String newTitle = detailData.has("title") ?
               detailData.get("title").asText() :
               detailData.get("name").asText();
         if (!newTitle.equals(item.getTitle())) {
            item.setTitle(newTitle);
            isUpdated = true;
         }
      }
      
      // 설명 업데이트
      if (detailData.has("overview")) {
         String newOverview = truncateOverview(detailData.get("overview").asText());
         if (!newOverview.equals(item.getItemDetail())) {
            item.setItemDetail(newOverview);
            isUpdated = true;
         }
      }
      
      // 평점 업데이트
      if (detailData.has("vote_average")) {
         double newVoteAverage = detailData.get("vote_average").asDouble();
         if (newVoteAverage != item.getVoteAverage()) {
            item.setVoteAverage(newVoteAverage);
            isUpdated = true;
         }
      }
      
      // 인기도 업데이트
      if (detailData.has("popularity")) {
         double newPopularity = detailData.get("popularity").asDouble();
         if (newPopularity != item.getPopularity()) {
            item.setPopularity(newPopularity);
            isUpdated = true;
         }
      }
      
      // 트레일러 정보 업데이트
      try {
         String videoUrl = String.format("%s/%s/%d/videos?api_key=%s&language=ko-KR",
               TMDB_BASE_URL,
               item.getMainGenre() == MainGenre.MOVIE ? "movie" : "tv",
               item.getTmdbId(),
               apiKey);
         
         ResponseEntity<String> videoResponse = restTemplate.getForEntity(videoUrl, String.class);
         JsonNode videoData = objectMapper.readTree(videoResponse.getBody());
         
         String newTrailerUrl = null;
         if (videoData.has("results") && videoData.get("results").isArray()) {
            JsonNode videos = videoData.get("results");
            for (JsonNode video : videos) {
               String type = video.get("type").asText();
               String site = video.get("site").asText();
               if ("Trailer".equals(type) && "YouTube".equals(site)) {
                  newTrailerUrl = "https://www.youtube.com/watch?v=" + video.get("key").asText();
                  break;
               }
            }
         }
         if ((newTrailerUrl != null && !newTrailerUrl.equals(item.getTrailerUrl())) ||
               (newTrailerUrl == null && item.getTrailerUrl() != null)) {
            item.setTrailerUrl(newTrailerUrl);
            isUpdated = true;
         }
      } catch (Exception e) {
         log.error("Error updating trailer for item {}: {}", item.getTitle(), e.getMessage());
      }
      
      // 제작국가 정보 업데이트
      if (detailData.has("production_countries")) {
         List<String> countries = new ArrayList<>();
         JsonNode productionCountries = detailData.get("production_countries");
         for (JsonNode country : productionCountries) {
            countries.add(country.get("name").asText());
         }
         String countryString = String.join(", ", countries);
         
         if (!countryString.equals(item.getProductionCountries())) {
            item.setProductionCountries(countryString);
            isUpdated = true;
         }
      }
      
      
      if (isUpdated) {
         log.info("Item updated: {}", item.getTitle());
      } else {
         log.debug("No update needed for item: {}", item.getTitle());
      }
   }
   
   
   // 주간 인기 콘텐츠 가져오기
   public List<ItemCrawlDto> getWeeklyTrending() {
      String url = String.format("%s/trending/all/week?api_key=%s&language=ko-KR",
            TMDB_BASE_URL, apiKey);
      return getTrendingItems(url, "weekly");
   }
   
   
   
   private List<ItemCrawlDto> getTrendingItems(String url, String period) {
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         JsonNode results = root.get("results");
         List<ItemCrawlDto> trendingItems = new ArrayList<>();
         
         for (JsonNode result : results) {
            try {
               Integer tmdbId = result.get("id").asInt();
               String mediaType = result.has("media_type") ?
                     result.get("media_type").asText() : "movie";
               
               // 상세 정보 가져오기
               String detailUrl = String.format("%s/%s/%d?api_key=%s&language=ko-KR",
                     TMDB_BASE_URL,
                     mediaType,
                     tmdbId,
                     apiKey);
               
               ResponseEntity<String> detailResponse = restTemplate.getForEntity(detailUrl, String.class);
               JsonNode detailData = objectMapper.readTree(detailResponse.getBody());
               
               ItemCrawlDto itemDto = new ItemCrawlDto();
               itemDto.setTmdbId(tmdbId);
               itemDto.setTitle(detailData.has("title") ?
                     detailData.get("title").asText() :
                     detailData.get("name").asText());
               itemDto.setVoteAverage(detailData.get("vote_average").asDouble());
               itemDto.setPopularity(detailData.get("popularity").asDouble());
               
               // 썸네일 설정
               if (detailData.has("poster_path")) {
                  ThumbnailDto thumbnailDto = new ThumbnailDto();
                  thumbnailDto.setThumbnailName(IMAGE_BASE_URL + detailData.get("poster_path").asText());
                  itemDto.setThumbnailDto(thumbnailDto);
               }
               
               trendingItems.add(itemDto);
               
               // API 호출 제한 방지
               Thread.sleep(100);
            } catch (Exception e) {
               log.error("Error processing trending item: {}", e.getMessage());
            }
         }
         
         log.info("Retrieved {} {} trending items", trendingItems.size(), period);
         return trendingItems;
         
      } catch (Exception e) {
         log.error("Error fetching {} trending items: {}", period, e.getMessage());
         return Collections.emptyList();
      }
   }
   
   // 최신 콘텐츠 TOP 3
   public List<ItemCrawlDto> getLatestContents() {
      String url = String.format("%s/discover/movie?api_key=%s&language=ko-KR&sort_by=release_date.desc&watch_region=KR&with_watch_monetization_types=flatrate&page=1",
            TMDB_BASE_URL, apiKey);
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         JsonNode results = root.get("results");
         List<ItemCrawlDto> items = new ArrayList<>();
         
         for (JsonNode result : results) {
            ItemCrawlDto itemDto = new ItemCrawlDto();
            itemDto.setTmdbId(result.get("id").asInt());
            itemDto.setTitle(result.get("title").asText());
            itemDto.setVoteAverage(result.get("vote_average").asDouble());
            
            if (result.has("poster_path")) {
               ThumbnailDto thumbnailDto = new ThumbnailDto();
               thumbnailDto.setThumbnailName(IMAGE_BASE_URL + result.get("poster_path").asText());
               itemDto.setThumbnailDto(thumbnailDto);
            }
            
            items.add(itemDto);
         }
         
         return items;
      } catch (Exception e) {
         log.error("Error fetching latest contents: {}", e.getMessage());
         return Collections.emptyList();
      }
   }
   
   // 전체 인기 TOP 10
   public List<ItemCrawlDto> getTop10Trending() {
      String url = String.format("%s/discover/movie?api_key=%s&language=ko-KR&sort_by=popularity.desc&watch_region=KR&with_watch_monetization_types=flatrate&page=1",
            TMDB_BASE_URL, apiKey);
      return getTrendingItems(url, "daily", MAX_ITEMS);
   }
   
   // 영화 TOP 10
   public List<ItemCrawlDto> getTop10Movies() {
      String url = String.format("%s/discover/movie?api_key=%s&language=ko-KR&sort_by=popularity.desc&include_adult=false&watch_region=KR&page=1",
            TMDB_BASE_URL, apiKey);
      return getTrendingItems(url, "movie", MAX_ITEMS);
   }
   
   // 드라마 TOP 10
   public List<ItemCrawlDto> getTop10TvShows() {
      String url = String.format("%s/discover/tv?api_key=%s&language=ko-KR&sort_by=popularity.desc&include_adult=false&watch_region=KR&with_watch_monetization_types=flatrate&page=1",
            TMDB_BASE_URL, apiKey);
      return getTrendingItems(url, "tv", MAX_ITEMS);
   }
   
   private List<ItemCrawlDto> getTrendingItems(String url, String type, int limit) {
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         JsonNode results = root.get("results");
         List<ItemCrawlDto> items = new ArrayList<>();
         int count = 0;
         
         for (JsonNode result : results) {
            if (count >= limit) break;
            
            try {
               // JSON 데이터 추출
               Integer tmdbId = result.get("id").asInt();
               String title = result.has("title") ?
                     result.get("title").asText() :
                     result.get("name").asText();
               double voteAverage = result.get("vote_average").asDouble();
               String releaseDate = result.has("release_date") ?
                     result.get("release_date").asText() :
                     (result.has("first_air_date") ? result.get("first_air_date").asText() : "N/A");
               String itemDetail = result.has("overview") ?
                     truncateOverview(result.get("overview").asText()) : "상세 설명이 없습니다.";
               
               // 장르 매핑
               SubGenre subGenre = SubGenre.ETC;
               if (result.has("genre_ids") && result.get("genre_ids").isArray()) {
                  JsonNode genreIds = result.get("genre_ids");
                  for (JsonNode genreId : genreIds) {
                     SubGenre mappedGenre = GENRE_MAPPING.get(genreId.asInt());
                     if (mappedGenre != null) {
                        subGenre = mappedGenre;
                        break;
                     }
                  }
               }
               
               // ItemFormDto 생성 및 데이터 설정
               ItemCrawlDto itemDto = new ItemCrawlDto();
               itemDto.setTmdbId(tmdbId);
               itemDto.setTitle(title);
               itemDto.setVoteAverage(voteAverage);
               itemDto.setReleaseDate(releaseDate);
               itemDto.setItemDetail(itemDetail);
               
               if (result.has("poster_path")) {
                  ThumbnailDto thumbnailDto = new ThumbnailDto();
                  thumbnailDto.setThumbnailName(IMAGE_BASE_URL + result.get("poster_path").asText());
                  itemDto.setThumbnailDto(thumbnailDto);
               }
               
               // ItemCrawl 생성 및 데이터 설정
               if (!itemCrawlRepository.existsByTmdbId(tmdbId)) {
                  ItemCrawl itemCrawl = new ItemCrawl();
                  itemCrawl.setTmdbId(tmdbId);
                  itemCrawl.setTitle(title);
                  itemCrawl.setVoteAverage(voteAverage);
                  itemCrawl.setReleaseDate(releaseDate);
                  itemCrawl.setItemDetail(itemDetail);
                  itemCrawl.setThumbnailUrl(result.has("poster_path") ?
                        IMAGE_BASE_URL + result.get("poster_path").asText() : null);
                  itemCrawl.setMainGenre(MainGenre.valueOf(type.toUpperCase()));
                  itemCrawl.setSubGenre(subGenre);
                  
                  // DB에 저장
                  itemCrawlRepository.save(itemCrawl);
               }
               
               // 리스트에 추가
               items.add(itemDto);
               count++;
            } catch (Exception e) {
               log.error("Error processing item: {}", e.getMessage());
            }
         }
         
         log.info("Retrieved {} {} trending items", items.size(), type);
         return items;
      } catch (Exception e) {
         log.error("Error fetching {} trending items: {}", type, e.getMessage());
         return Collections.emptyList();
      }
   }
   
   
   // 애니메이션 TOP 10
   public List<ItemCrawlDto> getTop10Animation() {
      // 애니메이션 검색 조건 강화
      String url = String.format("%s/discover/tv?api_key=%s&with_genres=16&language=ko-KR&sort_by=popularity.desc&include_adult=false&watch_region=KR&with_watch_monetization_types=flatrate&page=1",
            TMDB_BASE_URL, apiKey);
      List<ItemCrawlDto> items = getTrendingItems(url, "animation", MAX_ITEMS);
      
      // 결과가 충분하지 않으면 영화 애니메이션도 검색
      if (items.size() < MAX_ITEMS) {
         String movieUrl = String.format("%s/discover/movie?api_key=%s&with_genres=16&language=ko-KR&sort_by=popularity.desc&include_adult=false&watch_region=KR&with_watch_monetization_types=flatrate&page=1",
               TMDB_BASE_URL, apiKey);
         items.addAll(getTrendingItems(movieUrl, "animation", MAX_ITEMS - items.size()));
      }
      
      return items.subList(0, Math.min(items.size(), MAX_ITEMS));
   }
   
   // 예능 TOP 10
   public List<ItemCrawlDto> getTop10Entertainment() {
      // 예능 검색 조건 강화
      String url = String.format("%s/discover/tv?api_key=%s&with_genres=10764,10767&language=ko-KR&sort_by=popularity.desc&watch_region=KR&with_watch_monetization_types=flatrate&page=1",
            TMDB_BASE_URL, apiKey);
      return getTrendingItems(url, "entertainment", MAX_ITEMS);
   }
   
   
   // 월간 인기 콘텐츠 가져오기
   public List<ItemCrawlDto> getMonthlyTrending() {
      String url = String.format("%s/trending/all/week?api_key=%s&language=ko-KR&sort_by=popularity.desc",
            TMDB_BASE_URL, apiKey);
      
      try {
         ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
         JsonNode root = objectMapper.readTree(response.getBody());
         List<ItemCrawlDto> items = new ArrayList<>();
         
         if (root.has("results")) {
            JsonNode results = root.get("results");
            int count = 0;
            
            for (JsonNode result : results) {
               if (count >= MAX_ITEMS) break;
               
               ItemCrawlDto itemDto = new ItemCrawlDto();
               itemDto.setTmdbId(result.get("id").asInt());
               itemDto.setTitle(result.has("title") ?
                     result.get("title").asText() :
                     result.get("name").asText());
               itemDto.setVoteAverage(result.get("vote_average").asDouble());
               itemDto.setPopularity(result.get("popularity").asDouble());
               
               // MainGenre 설정
               String mediaType = result.get("media_type").asText();
               itemDto.setMainGenre(mediaType.equals("movie") ? MainGenre.MOVIE : MainGenre.DRAMA);
               
               // SubGenre 설정
               if (result.has("genre_ids") && result.get("genre_ids").isArray()) {
                  JsonNode genreIds = result.get("genre_ids");
                  for (JsonNode genreId : genreIds) {
                     SubGenre mappedGenre = GENRE_MAPPING.get(genreId.asInt());
                     if (mappedGenre != null) {
                        itemDto.setSubGenre(mappedGenre);
                        break;
                     }
                  }
               }
               
               if (itemDto.getSubGenre() == null) {
                  itemDto.setSubGenre(SubGenre.ETC);
               }
               
               items.add(itemDto);
               count++;
            }
         }
         
         return items;
      } catch (Exception e) {
         log.error("Error fetching monthly trending items: {}", e.getMessage());
         return new ArrayList<>();
      }
   }
   
}