package com.example.chillStream.service;

import com.example.chillStream.dto.*;
import com.example.chillStream.entity.*;
import com.example.chillStream.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final PreviewRepository previewRepository;
    private final VideoRepository videoRepository;
    private final ThumbnailRepository thumbnailRepository;
    private final ContentsService contentsService;
    private final ItemCrawlRepository itemCrawlRepository;
    
    //item 삭제 메서드
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        itemRepository.delete(item);
    }
    
    //상세 페이지 불러오는 메서드
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        
        Thumbnail thumbnail = thumbnailRepository.findByItemId(itemId);
        ThumbnailDto thumbnailDto = ThumbnailDto.of(thumbnail);
        
        Preview preview = previewRepository.findByItemId(itemId);
        PreviewDto previewDto = PreviewDto.of(preview);
        
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setThumbnailDto(thumbnailDto);
        itemFormDto.setPreviewDto(previewDto);
        
        return itemFormDto;
    }
    
    //item 저장 메서드
    public void saveItem(ItemFormDto itemFormDto, MultipartFile thumbnailFile, MultipartFile previewFile) throws Exception {
        Item item = itemFormDto.createItem();
        itemRepository.save(item);
        
        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setItem(item);
        contentsService.saveThumbnailFile(thumbnail, thumbnailFile);
        
        Preview preview = new Preview();
        preview.setItem(item);
        contentsService.savePreviewFile(preview, previewFile);
    }
    
    //item 업데이트 메서드
    public void updateItem(ItemFormDto itemFormDto, MultipartFile thumbnailFile, MultipartFile previewFile) throws Exception {
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        
        contentsService.updateThumbnail(itemFormDto.getId(), thumbnailFile);
        contentsService.updatePreview(itemFormDto.getId(), previewFile);
    }
    
    //컨텐츠 관리 페이지 (검색)
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }
    
    public List<ItemFormDto> getTop5ItemDtos() {
        List<Item> items = itemRepository.findTop5ByOrderByIdDesc();
        return items.stream()
              .map(item -> {
                  ItemFormDto itemDto = ItemFormDto.of(item);
                  Thumbnail thumbnail = thumbnailRepository.findByItemId(item.getId());
                  if (thumbnail != null) {
                      itemDto.setThumbnailDto(ThumbnailDto.of(thumbnail));
                  }
                  return itemDto;
              })
              .collect(Collectors.toList());
    }
    
    public List<ItemFormDto> getAllItemDtos() {
        List<Item> items = itemRepository.findTop10ByOrderByIdDesc();
        return items.stream()
              .map(item -> {
                  ItemFormDto itemDto = ItemFormDto.of(item);
                  Thumbnail thumbnail = thumbnailRepository.findByItemId(item.getId());
                  Preview preview = previewRepository.findByItemId(item.getId());
                  itemDto.setThumbnailDto(ThumbnailDto.of(thumbnail));
                  itemDto.setPreviewDto(PreviewDto.of(preview));
                  return itemDto;
              })
              .collect(Collectors.toList());
    }
    
    
    @Transactional(readOnly = true)
    public ItemCrawlDto getItemByTmdbId(Integer tmdbId) {
        // tmdbId로 엔티티 검색
        ItemCrawl itemCrawl = itemCrawlRepository.findByTmdbId(tmdbId)
              .orElseThrow(() -> new EntityNotFoundException("Item not found with TMDB ID: " + tmdbId));
        // 엔티티를 DTO로 변환
        return new ItemCrawlDto(itemCrawl);
    }
    
    public List<ItemCrawlDto> searchItemCrawls(ItemCrawlSearchDto searchDto) {
        return itemCrawlRepository.searchItemCrawls(searchDto)
              .stream()
              .map(ItemCrawlDto::fromEntity) // 엔티티를 DTO로 변환
              .toList();
    }
    
    
    public List<ItemFormDto> searchItems(ItemSearchDto itemSearchDto) {
        return itemRepository.searchItems(itemSearchDto)
              .stream()
              .map(ItemFormDto::fromEntity) // 엔티티를 DTO로 변환
              .toList();
    }
}
