package com.example.chillStream.repository;

import com.example.chillStream.dto.ItemCrawlSearchDto;
import com.example.chillStream.entity.ItemCrawl;

import java.util.List;

public interface ItemCrawlRepositoryCustom {
   List<ItemCrawl> searchItemCrawls(ItemCrawlSearchDto itemCrawlSearchDto);
}
