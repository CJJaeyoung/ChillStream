package com.example.chillStream.repository;

import com.example.chillStream.dto.ItemCrawlSearchDto;
import com.example.chillStream.dto.ItemSearchDto;
import com.example.chillStream.entity.Item;
import com.example.chillStream.entity.ItemCrawl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    
    List<Item> searchItems(ItemSearchDto itemSearchDto);
}
