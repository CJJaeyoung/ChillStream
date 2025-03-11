package com.example.chillStream.controller;




import com.example.chillStream.dto.BannerImgDto;
import com.example.chillStream.dto.BannnerFormDto;
import com.example.chillStream.entity.BannerImg;
import com.example.chillStream.entity.Banners;
import com.example.chillStream.service.BannerService;
import com.example.chillStream.service.ExcelService;
import com.example.chillStream.service.TMDBService;
import com.example.chillStream.service.VisitorService;
import com.example.chillStream.repository.BannerImgRepository;
import com.example.chillStream.service.SubscriptionService;
import com.example.chillStream.service.SubscribeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final ExcelService excelService;
    private final VisitorService visitorService;
    private final BannerService bannerService;
    private final BannerImgRepository bannerImgRepository;
    private final TMDBService tmdbService;
    private final SubscriptionService subscriptionService;
    private final SubscribeService subscribeService;


//엑셀 다운로드
    @GetMapping("/admin/analytics/download")
    public ResponseEntity<InputStreamResource> downloadExcel() {
        try {
            ByteArrayInputStream in = excelService.generateExcelReport();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=content-analysis.xlsx");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 대시보드
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalVisitors", visitorService.getTotalVisitorCount());
        model.addAttribute("monthlyVisitors", visitorService.getMonthlyStats());
        model.addAttribute("dailyVisitors", visitorService.getDailyStats());
        model.addAttribute("todayVisitors", visitorService.getTodayVisitorCount());
        model.addAttribute("recentDailyStats", visitorService.getRecentDailyStats());
        model.addAttribute("monthlyTrending", tmdbService.getMonthlyTrending());
        model.addAttribute("weeklyTrending", tmdbService.getWeeklyTrending());
        // Payment 데이터를 이용한 주간 매출 데이터
        Map<String, Long> weeklyRevenue = subscribeService.calculateWeeklyRevenue();
        model.addAttribute("weeklyRevenue", weeklyRevenue);
        // 총 매출 데이터 추가
        Long totalRevenue = subscribeService.calculateTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue);
        // 주간 총 매출 계산
        Long weeklyTotalRevenue = weeklyRevenue.values().stream().mapToLong(Long::longValue).sum();
        model.addAttribute("weeklyTotalRevenue", weeklyTotalRevenue);
        
        // 일일 매출 데이터
        Map<String, Long> dailyRevenue = subscribeService.calculateDailyRevenue();
        model.addAttribute("dailyRevenue", dailyRevenue);
        
        return "admin/dashboard";
    }

    // 배너 등록 페이지
    @GetMapping("/admin/banners/new")
    public String bannerForm(Model model) {
        model.addAttribute("bannnerFormDto", new BannnerFormDto());
        return "admin/banner/bannerForm";
    }   

    // 배너등록 
    @PostMapping("/admin/banners/new")
    public String bannerNew(@Valid BannnerFormDto bannnerFormDto, BindingResult bindingResult, @RequestParam("bannerImgFile") MultipartFile bannerImgFile, Model model) {
        if (bindingResult.hasErrors()) {  // 유효성 체크 
            return "admin/banner/bannerForm";
        }
        if(bannerImgFile.isEmpty()&&bannnerFormDto.getId() == null) {
            model.addAttribute("errorMessage", "배너 이미지는 필수 입력 값입니다.");
            return "admin/banner/bannerForm";
        }
        try {
           bannerService.saveBanner(bannnerFormDto, bannerImgFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "배너 등록 중 오류가 발생했습니다.");
            return "admin/banner/bannerForm";
        }
        return "redirect:/admin/banners";
    }

    // 배너 목록 페이지
    @GetMapping("/admin/banners")
    public String bannerList(Model model) {
        List<Banners> banners = bannerService.getBannerList();
        List<BannnerFormDto> bannerDtos = banners.stream()
            .map(banner -> {
                BannnerFormDto dto = BannnerFormDto.of(banner);
                if (banner.getBannerImg() != null) {
                    System.out.println("이미지 URL 확인: " + banner.getBannerImg().getImgUrl());  // URL 확인
                    dto.setBannerImgDto(BannerImgDto.of(banner.getBannerImg()));
                } else {
                    BannerImg bannerImg = bannerImgRepository.findByBannersId(banner.getId());
                    if (bannerImg != null) {
                        System.out.println("Repository에서 찾은 이미지 URL: " + bannerImg.getImgUrl());  // URL 확인
                        dto.setBannerImgDto(BannerImgDto.of(bannerImg));
                    }
                }
                return dto;
            })
            .collect(Collectors.toList());
        
        model.addAttribute("banners", bannerDtos);
        return "admin/banner/bannerList";
    }

    // 배너 삭제
    @DeleteMapping("/admin/banners/{bannerId}")
    @ResponseBody
    public ResponseEntity deleteBanner(@PathVariable("bannerId") Long bannerId){
        try{
        bannerService.deleteBanner(bannerId);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("삭제 중 오류가 발생했습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }

    // 배너 수정
    @PostMapping("/admin/banners/{bannerId}/modify")
    public String bannerUpdate(@Valid BannnerFormDto bannnerFormDto, BindingResult bindingResult, @RequestParam("bannerImgFile") MultipartFile bannerImgFile, Model model){
        if(bindingResult.hasErrors()){
            return "admin/banner/bannerForm";
        }
        if(bannerImgFile.isEmpty()&&bannnerFormDto.getId() == null){
            model.addAttribute("errorMessage", "배너 이미지는 필수 입력 값입니다.");
            return "admin/banner/bannerForm";
        }
        try{
            bannerService.updateBanner(bannnerFormDto, bannerImgFile);
        }
        catch(Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage", "배너 수정 중 오류가 발생했습니다.");
            return "admin/banner/bannerForm";
        }
        return "redirect:/admin/banners";
    }

    // 배너 수정 페이지
    @GetMapping("/admin/banners/{bannerId}/modify")
    public String bannerModifyForm(@PathVariable("bannerId") Long bannerId, Model model) {
        try {
            BannnerFormDto bannnerFormDto = bannerService.getBannerDtl(bannerId);
            model.addAttribute("bannnerFormDto", bannnerFormDto);
            return "admin/banner/bannerForm";
        } catch(EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 배너입니다.");
            return "admin/banner/bannerList";
        }
    }

   

}