package com.example.chillStream.runner;

import com.example.chillStream.constant.Subscription;
import com.example.chillStream.constant.SubscriptionStatus;
import com.example.chillStream.entity.MembershipPlan;
import com.example.chillStream.entity.Payment;
import com.example.chillStream.entity.Subscribe;
import com.example.chillStream.entity.Banners;
import com.example.chillStream.entity.BannerImg;
import com.example.chillStream.repository.MembershipPlanRepository;
import com.example.chillStream.repository.PaymentRepository;
import com.example.chillStream.repository.SubscribeRepository;
import com.example.chillStream.repository.BannersRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
   
   private final SubscribeRepository subscribeRepository;
   private final MembershipPlanRepository membershipPlanRepository;
   private final PaymentRepository paymentRepository;
   private final BannersRepository bannerRepository;
   
   public DataInitializer(SubscribeRepository subscribeRepository,
                          MembershipPlanRepository membershipPlanRepository,
                          PaymentRepository paymentRepository,
                          BannersRepository bannerRepository) {
      this.subscribeRepository = subscribeRepository;
      this.membershipPlanRepository = membershipPlanRepository;
      this.paymentRepository = paymentRepository;
      this.bannerRepository = bannerRepository;
   }
   
   @Override
   public void run(String... args) throws Exception {
      // 데이터 초기화
      if (subscribeRepository.count() == 0) {
         Subscribe subscribe1 = new Subscribe();
         subscribe1.setSubscribeNm("베이직 구독");
         subscribe1.setSubscribeDetail("합리적인 가격\n(광고기능 포함)");
         subscribe1.setSubscription(Subscription.BASIC);
         subscribe1.setPrice(5900);
         subscribeRepository.save(subscribe1);
         
         Subscribe subscribe2 = new Subscribe();
         subscribe2.setSubscribeNm("프리미엄 구독");
         subscribe2.setSubscribeDetail("광고없는 편안한 시청");
         subscribe2.setSubscription(Subscription.PREMIUM);
         subscribe2.setPrice(9900);
         subscribeRepository.save(subscribe2);
         
         System.out.println("기본 데이터가 초기화되었습니다.");
      }
      
      if (membershipPlanRepository.count() == 0) {
         List<MembershipPlan> membershipPlans = Arrays.asList(
               new MembershipPlan(LocalDate.of(2025, 2, 20), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2025, 3, 20), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2025, 4, 20), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2025, 2, 25), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2025, 3, 30), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2025, 4, 10), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2025, 2, 15), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2025, 2, 28), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2025, 3, 15), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2025, 4, 5), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2025, 5, 10), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2025, 6, 15), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2025, 7, 20), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2025, 8, 25), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2025, 9, 30), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2025, 10, 5), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2025, 11, 10), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2025, 12, 15), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2026, 1, 20), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2026, 2, 25), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2026, 3, 30), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2026, 4, 10), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2026, 5, 15), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2026, 6, 20), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2026, 7, 25), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2026, 8, 30), SubscriptionStatus.PAUSED),
               new MembershipPlan(LocalDate.of(2026, 9, 10), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2026, 10, 15), SubscriptionStatus.CANCELLED),
               new MembershipPlan(LocalDate.of(2026, 11, 20), SubscriptionStatus.ACTIVE),
               new MembershipPlan(LocalDate.of(2026, 12, 25), SubscriptionStatus.PAUSED)
         );
         
         membershipPlanRepository.saveAll(membershipPlans);
         
         System.out.println("기본 MembershipPlan 데이터가 초기화되었습니다.");
         
         List<Payment> payments = Arrays.asList(
               new Payment("Basic Plan", "imp_301", "order_301", 6900, "BASIC", "John Doe", "john.doe@example.com", "1234567890", LocalDateTime.now(), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_302", "order_302", 9900, "INACTIVE", "Jane Doe", "jane.doe@example.com", "1234567891", LocalDateTime.now().minusDays(1), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_303", "order_303", 9900, "PREMIUM", "Alice Smith", "alice.smith@example.com", "1234567892", LocalDateTime.now().minusDays(2), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_304", "order_304", 6900, "BASIC", "Bob Johnson", "bob.johnson@example.com", "1234567893", LocalDateTime.now().minusDays(3), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_305", "order_305", 6900, "INACTIVE", "Charlie Brown", "charlie.brown@example.com", "1234567894", LocalDateTime.now().minusDays(4), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_306", "order_306", 9900, "PREMIUM", "Diana Prince", "diana.prince@example.com", "1234567895", LocalDateTime.now().minusDays(5), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_307", "order_307", 6900, "BASIC", "Eve Davis", "eve.davis@example.com", "1234567896", LocalDateTime.now().minusDays(6), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_308", "order_308", 9900, "INACTIVE", "Frank Miller", "frank.miller@example.com", "1234567897", LocalDateTime.of(2025, 1, 14, 17, 0), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_309", "order_309", 9900, "PREMIUM", "Grace Hopper", "grace.hopper@example.com", "1234567898", LocalDateTime.of(2025, 1, 15, 18, 0), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_310", "order_310", 6900, "BASIC", "Henry Ford", "henry.ford@example.com", "1234567899", LocalDateTime.of(2025, 1, 16, 19, 0), Subscription.BASIC),
               new Payment("Basic Plan", "imp_311", "order_311", 6900, "BASIC", "Anna Lee", "anna.lee@example.com", "1234567800", LocalDateTime.of(2025, 1, 17, 10, 0), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_312", "order_312", 9900, "INACTIVE", "Mark Twain", "mark.twain@example.com", "1234567801", LocalDateTime.of(2025, 1, 20, 11, 0), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_313", "order_313", 9900, "PREMIUM", "Sophia White", "sophia.white@example.com", "1234567802", LocalDateTime.of(2025, 1, 21, 12, 0), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_314", "order_314", 6900, "BASIC", "James Black", "james.black@example.com", "1234567803", LocalDateTime.of(2025, 1, 21, 13, 0), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_315", "order_315", 6900, "INACTIVE", "Emily Clark", "emily.clark@example.com", "1234567804", LocalDateTime.of(2025, 1, 21, 14, 0), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_316", "order_316", 9900, "PREMIUM", "Oliver Stone", "oliver.stone@example.com", "1234567805", LocalDateTime.of(2025, 1, 21, 15, 0), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_317", "order_317", 6900, "BASIC", "Lucy Hale", "lucy.hale@example.com", "1234567806", LocalDateTime.of(2025, 1, 21, 16, 0), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_318", "order_318", 9900, "INACTIVE", "Harry Potter", "harry.potter@example.com", "1234567807", LocalDateTime.of(2025, 1, 21, 17, 0), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_319", "order_319", 9900, "PREMIUM", "Ginny Weasley", "ginny.weasley@example.com", "1234567808", LocalDateTime.of(2025, 1, 21, 18, 0), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_320", "order_320", 6900, "BASIC", "Ron Weasley", "ron.weasley@example.com", "1234567809", LocalDateTime.of(2025, 1, 21, 19, 0), Subscription.BASIC),
               new Payment("Basic Plan", "imp_321", "order_321", 6900, "BASIC", "Luna Lovegood", "luna.lovegood@example.com", "1234567810", LocalDateTime.of(2025, 1, 22, 10, 0), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_322", "order_322", 9900, "INACTIVE", "Neville Longbottom", "neville.longbottom@example.com", "1234567811", LocalDateTime.of(2025, 1, 22, 11, 0), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_323", "order_323", 9900, "PREMIUM", "Draco Malfoy", "draco.malfoy@example.com", "1234567812", LocalDateTime.of(2025, 1, 22, 12, 0), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_324", "order_324", 6900, "BASIC", "Severus Snape", "severus.snape@example.com", "1234567813", LocalDateTime.of(2025, 1, 22, 13, 0), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_325", "order_325", 6900, "INACTIVE", "Albus Dumbledore", "albus.dumbledore@example.com", "1234567814", LocalDateTime.of(2025, 1, 22, 14, 0), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_326", "order_326", 9900, "PREMIUM", "Tom Riddle", "tom.riddle@example.com", "1234567815", LocalDateTime.of(2025, 1, 22, 15, 0), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_327", "order_327", 6900, "BASIC", "Bellatrix Lestrange", "bellatrix@example.com", "1234567816", LocalDateTime.of(2025, 1, 22, 16, 0), Subscription.BASIC),
               new Payment("Inactive Plan", "imp_328", "order_328", 9900, "INACTIVE", "Sirius Black", "sirius.black@example.com", "1234567817", LocalDateTime.of(2025, 1, 22, 17, 0), Subscription.INACTIVE),
               new Payment("Premium Plan", "imp_329", "order_329", 9900, "PREMIUM", "Remus Lupin", "remus.lupin@example.com", "1234567818", LocalDateTime.of(2025, 1, 22, 18, 0), Subscription.PREMIUM),
               new Payment("Basic Plan", "imp_330", "order_330", 6900, "BASIC", "Nymphadora Tonks", "nymphadora@example.com", "1234567819", LocalDateTime.of(2025, 1, 22, 19, 0), Subscription.BASIC)
         );
         
         for (int i = 0; i < payments.size(); i++) {
            payments.get(i).setMembershipPlan(membershipPlans.get(i));
         }
         
         paymentRepository.saveAll(payments);
         
         System.out.println("기본 Payment 데이터가 초기화되었습니다.");
      }
      
      // 배너 초기 데이터 추가
      if (bannerRepository.count() == 0) {
        
         
         BannerImg bannerImg = new BannerImg();
         bannerImg.setImgName("322236_455901_4735");
         bannerImg.setOriImgName("322236_455901_4735.jpg");
         bannerImg.setImgUrl("/322236_455901_4735.jpg");
         
         System.out.println("기본 배너가 초기화되었습니다.");
      }
   }
}
