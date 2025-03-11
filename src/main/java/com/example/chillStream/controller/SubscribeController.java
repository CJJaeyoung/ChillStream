package com.example.chillStream.controller;


import com.example.chillStream.config.SecurityUtil;
import com.example.chillStream.constant.Role;
import com.example.chillStream.dto.SubscribeFormDto;
import com.example.chillStream.entity.Member;
import com.example.chillStream.entity.Subscribe;
import com.example.chillStream.repository.MemberRepository;
import com.example.chillStream.service.SubscribeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.List;

import com.example.chillStream.dto.PaymentRequestDto;

@Controller
@RequiredArgsConstructor
public class SubscribeController {
   
   private final SubscribeService subscribeService;
   private final MemberRepository memberRepository;



   @GetMapping(value = "/admin/subscribe/new")
   public String subscribeForm(Model model) {
      model.addAttribute("subscribeFormDto", new SubscribeFormDto());
      return "subscribe/subscribeForm";
   }

   @PostMapping(value = "/admin/subscribe/new")
   public String subscribeNew(@Valid SubscribeFormDto subscribeFormDto, BindingResult bindingResult,
                              Model model) {
      if (bindingResult.hasErrors()) {
         return "subscribe/subscribeForm";
      }

      try {
         subscribeService.saveSubscribe(subscribeFormDto);
      } catch (Exception e) {
         model.addAttribute("errorMessage",
                 "상품 등록 중 에러가 발생하였습니다.");
         return "subscribe/subscribeForm";
      }
      return "redirect:/subscribe/buy";
   }
   
   @GetMapping(value = "/admin/subscribe/{subscribeId}")
   public String subscribeDtl(@PathVariable("subscribeId")Long subscribeId, Model model){
      try {
         SubscribeFormDto subscribeFormDto = subscribeService.getSubscribeEdit(subscribeId);
         model.addAttribute("subscribeFormDto", subscribeFormDto);
      }catch (EntityNotFoundException e){
         model.addAttribute("errorMessage","존재하지 않는 상품입니다.");
         model.addAttribute("subscribeFormDto",new SubscribeFormDto());
         return "subscribe/subscribeForm";
      }
      return "subscribe/subscribeForm";
   }
   
   @PostMapping(value = "/admin/subscribe/{subscribeId}")
   public String subscribeUpdate(@Valid SubscribeFormDto subscribeFormDto, BindingResult bindingResult,
                                 Model model){
      if(bindingResult.hasErrors()){
         return "subscribe/subscribeForm";
      }
      try {
         subscribeService.updateSubscribe(subscribeFormDto);
         model.addAttribute("successMessage", "상품이 성공적으로 수정되었습니다.");
      }catch (Exception e){
         model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
         return "subscribe/subscribeForm";
      }
      return "redirect:/subscribe/buy";
   }
   
   @PostMapping(value = "/admin/subscribe/delete/{subscribeId}")
   public String deleteSubscribe(@PathVariable("subscribeId") Long subscribeId, Model model) {
      try {
         subscribeService.deleteSubscribe(subscribeId); // 호출
         model.addAttribute("successMessage", "상품이 성공적으로 삭제되었습니다.");
      } catch (EntityNotFoundException e) {
         model.addAttribute("errorMessage", "삭제하려는 상품이 존재하지 않습니다.");
      } catch (Exception e) {
         model.addAttribute("errorMessage", "상품 삭제 중 에러가 발생하였습니다.");
      }
      return "redirect:/subscribe/buy";
   }
   
   
   @GetMapping("/subscribe/buy")
   public String subscribeBuy(Model model) {
      String email = SecurityUtil.getCurrentUserEmail();
      
      if (email == null) {
         throw new IllegalStateException("로그인된 사용자의 이메일 정보를 가져올 수 없습니다.");
      }
      
      Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));
      
      List<SubscribeFormDto> subscribes = subscribeService.getAllSubscribeDtos();

      boolean isAdmin = member.getRole() == Role.ADMIN;


      model.addAttribute("currentUserEmail", email);
      model.addAttribute("currentUserName", member.getName());
      model.addAttribute("currentUserTel", member.getTel());
      model.addAttribute("isAdmin", isAdmin);
      model.addAttribute("subscribes", subscribes);
      model.addAttribute("subscribeFormDto", new SubscribeFormDto());
      
      return "subscribe/subscribeBuy";
   }

   @GetMapping("/subscribe/{id}")
   public ResponseEntity<Subscribe> getSubscribeById(@PathVariable Long id) {
      Subscribe subscribe = subscribeService.findById(id);
      if (subscribe == null) {
         return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(subscribe);
   }


   @GetMapping("/subscribeBuy")
   public String getSubscribePage(Model model, @AuthenticationPrincipal Member member) {

      if (member == null) {
         throw new IllegalArgumentException("로그인된 사용자가 없습니다.");
      }

      model.addAttribute("member", member); // 로그인된 사용자 정보 전달
      model.addAttribute("subscribes", subscribeService.getAllSubscribes()); // 구독 정보 추가
      return "subscribe/subscribeBuy";
   }
   
   
   @GetMapping(value = "/subscription/check")
   public ResponseEntity checkSubs(Principal principal){
      String email = SecurityUtil.getCurrentUserEmail();
      String membersSubs;
      try {
         membersSubs = subscribeService.checkSubscription(email);
         System.out.println(membersSubs);
      }
      catch (Exception e){
         e.printStackTrace();
         return new ResponseEntity<String>("에러발생", HttpStatus.BAD_REQUEST);
      }
      return new ResponseEntity<String>(membersSubs,HttpStatus.OK);
   }
   

}