package com.example.chillStream.controller;

import com.example.chillStream.dto.MovieSelectionRequestDto;
import com.example.chillStream.entity.ItemCrawl;
import com.example.chillStream.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // 랜덤 영화 데이터 반환
    @GetMapping("/random")
    public List<ItemCrawl> getRandomMovies() {
        return movieService.getRandomItems(15); // 랜덤으로 15개의 영화 반환
    }

    @PostMapping("/save-selection")
    public ResponseEntity<Void> saveUserSelections(
            @RequestParam Long memberId,
            @RequestBody List<MovieSelectionRequestDto> selections) {
        System.out.println("받은 memberId: " + memberId);
        System.out.println("받은 데이터: " + selections);
        movieService.saveUserSelect(selections);
        return ResponseEntity.ok().build();
    }

}
