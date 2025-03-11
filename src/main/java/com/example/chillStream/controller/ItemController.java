package com.example.chillStream.controller;

import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.dto.ItemCrawlDto;
import com.example.chillStream.dto.ItemFormDto;
import com.example.chillStream.dto.ItemSearchDto;
import com.example.chillStream.dto.VideoFormDto;
import com.example.chillStream.entity.Item;
import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.Video;
import com.example.chillStream.service.ItemService;
import com.example.chillStream.service.MemberService;
import com.example.chillStream.service.RecommendationService;
import com.example.chillStream.service.VideoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    
    private final ItemService itemService;
    private final RecommendationService recommendationService;
    private final MemberService memberService;
    private final VideoService videoService;
    
    private Member getLoggedInMember(Principal principal) {
        String email = SecurityUtil.getCurrentUserEmail();
        return memberService.findByEmail(email)
              .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }
    
    //컨텐츠 삭제(ajax)
    @DeleteMapping("/admin/item/{itemId}")
    @ResponseBody
    public ResponseEntity deleteItem(@PathVariable("itemId")Long itemId){
        try{
            itemService.deleteItem(itemId);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("에러 발생", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }
    
    //관리자 컨텐츠 수정(post)
    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
                             @RequestParam("previewFile") MultipartFile previewFile,
                             Model model){
        if(bindingResult.hasErrors()){ // 유효성 체크
            return "item/itemForm";
        }
        if(thumbnailFile.isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","썸네일을 등록해주세요");
            return "item/itemForm";
        }
        if(previewFile.isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","미리보기를 등록해주세요");
            return "item/itemForm";
        }
        
        try{
            itemService.updateItem(itemFormDto, thumbnailFile, previewFile);
        }
        catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage","상품 업로드 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/admin/items";
    }
    
    // 관리자 콘텐츠 수정(get)
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemMngDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("itemFormDto", itemFormDto);
        return "item/itemForm";
    }
    
    // 관리자 컨텐츠 관리 페이지(게시판)
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemMng(@PathVariable("page")Optional<Integer> page, ItemSearchDto itemSearchDto, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        model.addAttribute("maxPage",10);
        return "item/itemMng";
    }
    
    
    // 상세 페이지 접속
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId) {
        try {
            String email = SecurityUtil.getCurrentUserEmail();
            Member member = memberService.findMemberByEmail(email);
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            List<ItemCrawlDto> recommendedContents = recommendationService.getFive(member.getId());
            List<VideoFormDto> videoList = videoService.getVideoFormList(itemId);
            
            model.addAttribute("memberId", member.getId());
            model.addAttribute("email", email);
            model.addAttribute("itemFormDto", itemFormDto);
            model.addAttribute("recommendedContents", recommendedContents);
            model.addAttribute("videoList", videoList);
            return "item/itemDtl";
        } catch(EntityNotFoundException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "item/itemDtl";
        }
    }
    
    // 콘텐츠 등록 Get
    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }
    
    // 콘텐츠 등록 Post
    @PostMapping(value = "admin/item/new")
    public String itemUpload(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
                             @RequestParam("previewFile") MultipartFile previewFile,
                             Model model){
        if(bindingResult.hasErrors()){ // 유효성 체크
            return "item/itemForm";
        }
        if(thumbnailFile.isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","썸네일을 등록해주세요");
            return "item/itemForm";
        }
        if(previewFile.isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage","미리보기를 등록해주세요");
            return "item/itemForm";
        }
        try{
            itemService.saveItem(itemFormDto, thumbnailFile, previewFile);
        }
        catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage","상품 업로드 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        return "redirect:/admin/items";
    }
    
}

