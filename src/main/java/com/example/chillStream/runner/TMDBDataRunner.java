package com.example.chillStream.runner;

import com.example.chillStream.service.TMDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TMDBDataRunner implements CommandLineRunner {
   
   private final TMDBService tmdbService;
   
   @Override
   public void run(String... args) throws Exception {
      // DB가 비어있을 때만 실행하도록 조건 추가 가능
      tmdbService.fetchAndSaveContent();
//      tmdbService.updateCrawledDataWithTMDB();
   }
}