package com.example.chillStream.controller;

import com.example.chillStream.dto.AdDto;
import com.example.chillStream.dto.AdSearchDto;
import com.example.chillStream.entity.Ad;
import com.example.chillStream.service.AdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdController {
    
    private final AdService adService;
    
    //광고 수정(get)
    @GetMapping("/ad/{adId}")
    public String adEditPage(Model model, @PathVariable("adId")Long adId){
        AdDto adDto = adService.getAdDtl(adId);
        model.addAttribute("adDto", adDto);
        return "ad/adForm";
    }
    
    //광고 수정(post)
    @PostMapping("/ad/{adId}")
    public String adEdit(@Valid AdDto adDto, BindingResult bindingResult, Model model, @RequestParam MultipartFile adFile){
        if(bindingResult.hasErrors()){ //유효성 검사
            return "ad/adForm";
        }
        try{
            adService.updateAd(adDto, adFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage","에러 발생");
        }
        return "redirect:/admin/ads";
    }
    
    //광고 삭제
    @DeleteMapping("/ad/{adId}")
    @ResponseBody
    public ResponseEntity deleteItem(@PathVariable("adId")Long adId){
        try{
            adService.deleteAd(adId);
        }
        catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("에러 발생", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }
    
    //광고 관리 페이지
    @GetMapping({"/ads","/ads/{page}"})
    public String adMngPage(@PathVariable("page")Optional<Integer> page, AdSearchDto adSearchDto, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        
        Page<Ad> ads = adService.getAdminItemPage(adSearchDto, pageable);
        model.addAttribute("ads",ads);
        model.addAttribute("adSearchDto",adSearchDto);
        model.addAttribute("maxPage",10);
        
        return "ad/adMng";
    }
    
    //광고 등록(Get)
    @GetMapping(value = "/ad/new")
    public String adRegistration(Model model){
        model.addAttribute("adDto",new AdDto());
        return "ad/adForm";
    }
    
    //광고 등록(Post)
    @PostMapping(value = "/ad/new")
    public String adRegistration(@Valid AdDto adDto, BindingResult bindingResult, Model model, @RequestParam MultipartFile adFile){
        if(bindingResult.hasErrors()){
            return "ad/adForm";
        }
        try{
            adService.saveAd(adDto, adFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage","에러 발생");
        }
        return "redirect:/admin/ads";
    }
}
