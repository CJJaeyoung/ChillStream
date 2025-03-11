package com.example.chillStream.repository;

import com.example.chillStream.constant.MainGenre;
import com.example.chillStream.dto.ItemCrawlSearchDto;
import com.example.chillStream.entity.ItemCrawl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCrawlRepository extends JpaRepository<ItemCrawl, Long>, ItemCrawlRepositoryCustom {
    boolean existsByTmdbId(Integer tmdbId);
    List<ItemCrawl> findByMainGenreOrderByIdDesc(MainGenre mainGenre);
    Optional<ItemCrawl> findByTmdbId(Integer tmdbId);
    List<ItemCrawl> findAllByOrderByIdDesc();
    
} 