# 🎬 Chillstream
- 사용자에게 다양한 비디오 콘텐츠를 제공하는 온라인 스트리밍 플랫폼
- 독 기반의 비즈니스 모델을 채택하고 있으며, 사용자들이 다양한 영화, 비디오 콘텐츠를 시청하고 댓글을 통해 소통할 수 있는 환경을 제공
- 개인화된 추천 시스템과 소셜 기능으로 사용자 경험 극대화
- 3명의 팀워크를 바탕으로 개발된 프로젝트

## 🧑🧑👩 CONTRIBUTORS
|팀원명   |포지션   | 담당   |깃허브 링크|
|---| ---   |---|---|
|현영민|   팀장|콘텐츠 관리 기능<br>영상 상세보기 기능<br>영상 및 광고 관리 기능<br>댓글 관리 및 무한 스크롤 기능<br>이어서 시청하기 및 자막 기능    | https://github.com/YMH2154 |
|최재영|   팀원 |회원가입 및 로그인 기능(소셜 API 이용)<br>아이디/비밀번호 찾기 기능<br>결제 관리 기능<br>콘텐츠 검색 기능<br>머지 수정 및 관리 역활   | https://github.com/CJJaeyoung/|
|김민주|   팀원|    번역 기능<br>배너 관리 기능<br>채팅 및 챗봇 AI 기능 <br>통계 및 엑셀 다운로드 기능<br>클릭률 기반 콘텐츠 추천 기능 |   https://github.com/dellymnzzu|





## 🚀 배포환경  
* AWS
* ~~- http://3.36.239.38/~~
#### ~~본 사이트는 현재 서버비용문제로 현재 비활성화되었습니다.~~


## ✅ 주요 기능
### 비디오 스트리밍 서비스
- **고품질 스트리밍**: Full HD부터 4K까지 다양한 해상도 지원
- **멀티 디바이스 지원**: 웹, 모바일, 태블릿 등 다양한 디바이스에서 접속 가능
- **이어보기 기능**: 사용자가 마지막으로 시청한 지점부터 이어서 시청 가능
- **오프라인 다운로드**: 프리미엄 사용자를 위한 콘텐츠 다운로드 기능


### 사용자 계정 관리
- **프로필 설정**: 사용자 정보 및 취향 설정
- **시청 기록 및 관심 목록**: 시청한 콘텐츠 및 관심 콘텐츠 관리
- **알림 설정**: 새로운 콘텐츠, 이벤트 등에 대한 알림 설정
- **소셜 로그인**: Google, Facebook, Naver 등을 통한 간편 로그인

### 구독 서비스
- **다양한 멤버십 플랜**: 기본, 프리미엄, 가족 등 다양한 구독 옵션
- **구독 관리**: 구독 상태 확인 및 변경
- **결제 내역**: 과거 결제 내역 조회 및 관리
- **자동 갱신 설정**: 구독 자동 갱신 설정 및 관리

### 커뮤니티 기능
- **댓글 시스템**: 콘텐츠에 대한 의견 공유
- **평점 기능**: 콘텐츠에 대한 평가 시스템
- **실시간 채팅**: 다른 사용자와의 소통 및 토론
- **소셜 공유**: SNS를 통한 콘텐츠 공유

### 관리자 기능
- **콘텐츠 관리**: 콘텐츠 등록, 수정, 삭제
- **사용자 관리**: 사용자 정보 관리 및 권한 설정
- **통계 대시보드**: 사용자 접속, 시청 통계 등 다양한
- **배너 및 광고 관리**: 사이트 내 프로모션 관리


## 🛠  사용기술
### ⚙️ 백엔드
- **언어 및 프레임워크**:
  - Java 21
  - Spring Boot 3.4.1
  - Spring Security (인증 및 권한 관리)
  - Spring Data JPA (데이터 접근 계층)
  - QueryDSL (타입 안전 쿼리)

- **데이터베이스**:
  - MySQL (주 데이터베이스)
  - H2 Database (테스트용)

- **실시간 통신**:
  - WebSocket (실시간 채팅)
  - STOMP (메시징 프로토콜)

- **인증 및 권한**:
  - OAuth2 (소셜 로그인)
  - JWT (토큰 기반 인증)

### 💻 프론트엔드
- **템플릿 엔진**:
  - Thymeleaf (서버 사이드 템플릿 엔진)

- **웹 기술**:
  - HTML5
  - CSS3/SASS
  - JavaScript (ES6+)
  - jQuery

- **UI 프레임워크**:
  - Bootstrap 5
  - Responsive Design
  


## 🛠  기타기능
- **다국어 지원**:
  - Google Cloud Translation API

- **알림 서비스**:
  - Spring Mail (이메일 알림)
  - WebSocket (실시간 알림)

- **결제 시스템**:
  - IamPort (결제 게이트웨이 연동)

- **데이터 처리**:
  - Apache POI (엑셀 파일 처리)
  - Schedule Tasks (자동화된 작업 스케줄링)



## 📦구조

<details><summary>📊 데이터베이스
</summary>

![Image](https://github.com/user-attachments/assets/51f6da9e-c41f-44ac-bdd0-af3e7bbf2d86)
## 



</details>

<details><summary>📦 디렉토리 구조
</summary>


```
📂src
 └─📂main
    ├─📂java
    │  └─📂com
    │      └─📂example
    │          └─📂chillStream
    │              ├─📜ChillStreamApplication.java
    │              ├─📂config
    │              │  ├─📜AuditConfig.java
    │              │  ├─📜AuditorAwareImpl.java
    │              │  ├─📜CustomAuthenticationEntryPoint.java
    │              │  ├─📜CustomHandshakeInterceptor.java
    │              │  ├─📜CustomOAuth2UserService.java
    │              │  ├─📜GoogleTranslationConfig.java
    │              │  ├─📜MailConfig.java
    │              │  ├─📜OAuthAttributes.java
    │              │  ├─📜RestTemplateConfig.java
    │              │  ├─📜SecurityConfig.java
    │              │  ├─📜SecurityUtil.java
    │              │  ├─📜VisitorInterceptor.java
    │              │  ├─📜WebMvcConfig.java
    │              │  └─📜WebSocketConfig.java
    │              ├─📂constant
    │              │  ├─📜Country.java
    │              │  ├─📜MainGenre.java
    │              │  ├─📜Role.java
    │              │  ├─📜SubGenre.java
    │              │  ├─📜Subscription.java
    │              │  └─📜SubscriptionStatus.java
    │              ├─📂controller
    │              │  ├─📜AdController.java
    │              │  ├─📜AdminController.java
    │              │  ├─📜ChatbotController.java
    │              │  ├─📜ChatController.java
    │              │  ├─📜CommentController.java
    │              │  ├─📜CrawlItemController.java
    │              │  ├─📜HomeController.java
    │              │  ├─📜ItemController.java
    │              │  ├─📜LegalController.java
    │              │  ├─📜MainController.java
    │              │  ├─📜MemberController.java
    │              │  ├─📜MovieController.java
    │              │  ├─📜PaymentController.java
    │              │  ├─📜SubscribeController.java
    │              │  ├─📜TermsController.java
    │              │  ├─📜TranslationController.java
    │              │  ├─📜VideoController.java
    │              │  ├─📜VideoProgressController.java
    │              │  └─📜WatchBoardController.java
    │              ├─📂dto
    │              │  ├─📜AdDto.java
    │              │  ├─📜AdSearchDto.java
    │              │  ├─📜BannerImgDto.java
    │              │  ├─📜BannnerFormDto.java
    │              │  ├─📜ChatNotification.java
    │              │  ├─📜ChatRoomDto.java
    │              │  ├─📜CommentCrawlDto.java
    │              │  ├─📜CommentDto.java
    │              │  ├─📜CommentFormDto.java
    │              │  ├─📜ItemCrawlDto.java
    │              │  ├─📜ItemCrawlSearchDto.java
    │              │  ├─📜ItemFormDto.java
    │              │  ├─📜ItemSearchDto.java
    │              │  ├─📜MainSubscribeDto.java
    │              │  ├─📜MemberDto.java
    │              │  ├─📜MemberFormDto.java
    │              │  ├─📜MemberPasswordDto.java
    │              │  ├─📜MemberUpdateFormDto.java
    │              │  ├─📜MovieSelectionRequestDto.java
    │              │  ├─📜PasswordChangeFormDto.java
    │              │  ├─📜PaymentRequest.java
    │              │  ├─📜PaymentRequestDto.java
    │              │  ├─📜PreviewDto.java
    │              │  ├─📜SessionUser.java
    │              │  ├─📜SubscribeFormDto.java
    │              │  ├─📜SubtitleDto.java
    │              │  ├─📜ThumbnailDto.java
    │              │  ├─📜VideoDto.java
    │              │  ├─📜VideoFormDto.java
    │              │  ├─📜VideoProgressDto.java
    │              │  └─📜VideoSearchDto.java
    │              ├─📂entity
    │              │  ├─📜Ad.java
    │              │  ├─📜BannerImg.java
    │              │  ├─📜Banners.java
    │              │  ├─📜BaseEntity.java
    │              │  ├─📜BaseTimeEntity.java
    │              │  ├─📜ChatMessage.java
    │              │  ├─📜ChatRoom.java
    │              │  ├─📜Comment.java
    │              │  ├─📜CommentCrawl.java
    │              │  ├─📜Content.java
    │              │  ├─📜Item.java
    │              │  ├─📜ItemCrawl.java
    │              │  ├─📜Member.java
    │              │  ├─📜MembershipPlan.java
    │              │  ├─📜Payment.java
    │              │  ├─📜Preview.java
    │              │  ├─📜Subscribe.java
    │              │  ├─📜Subscription.java
    │              │  ├─📜Subtitle.java
    │              │  ├─📜Thumbnail.java
    │              │  ├─📜UserMovieSelection.java
    │              │  ├─📜Video.java
    │              │  ├─📜VideoProgress.java
    │              │  └─📜VisitorCount.java
    │              ├─📂exception
    │              │  ├─📜NoAdException.java
    │              │  └─📜WebSocketExceptionHandler.java
    │              ├─📂repository
    │              │  ├─📜AdRepository.java
    │              │  ├─📜AdRepositoryCustom.java
    │              │  ├─📜AdRepositoryCustomImpl.java
    │              │  ├─📜BannerImgRepository.java
    │              │  ├─📜BannersRepository.java
    │              │  ├─📜ChatMessageRepository.java
    │              │  ├─📜ChatRoomRepository.java
    │              │  ├─📜CommentCrawlRepository.java
    │              │  ├─📜CommentRepository.java
    │              │  ├─📜ContentRepository.java
    │              │  ├─📜ItemCrawlRepository.java
    │              │  ├─📜ItemCrawlRepositoryCustom.java
    │              │  ├─📜ItemCrawlRepositoryImpl.java
    │              │  ├─📜ItemRepository.java
    │              │  ├─📜ItemRepositoryCustom.java
    │              │  ├─📜ItemRepositoryCustomImpl.java
    │              │  ├─📜MemberRepository.java
    │              │  ├─📜MembershipPlanRepository.java
    │              │  ├─📜PaymentRepository.java
    │              │  ├─📜PreviewRepository.java
    │              │  ├─📜SubscribeRepository.java
    │              │  ├─📜SubscriptionRepository.java
    │              │  ├─📜SubtitleRepository.java
    │              │  ├─📜ThumbnailRepository.java
    │              │  ├─📜UserMovieSelectionRepository.java
    │              │  ├─📜VideoProgressRepository.java
    │              │  ├─📜VideoRepository.java
    │              │  ├─📜VideoRepositoryCustom.java
    │              │  ├─📜VideoRepositoryCustomImpl.java
    │              │  ├─📜VisitorCountRepository.java
    │              │  └─📜VisitorRepository.java
    │              ├─📂runner
    │              │  ├─📜DataInitializer.java
    │              │  └─📜TMDBDataRunner.java
    │              ├─📂service
    │              │  ├─📜AdService.java
    │              │  ├─📜BannerService.java
    │              │  ├─📜ChatbotService.java
    │              │  ├─📜ChatService.java
    │              │  ├─📜CommentService.java
    │              │  ├─📜ContentsService.java
    │              │  ├─📜ExcelService.java
    │              │  ├─📜FileService.java
    │              │  ├─📜ItemCrawlService.java
    │              │  ├─📜ItemService.java
    │              │  ├─📜MailService.java
    │              │  ├─📜MemberService.java
    │              │  ├─📜MovieService.java
    │              │  ├─📜PaymentService.java
    │              │  ├─📜RecommendationService.java
    │              │  ├─📜RefundService.java
    │              │  ├─📜SubscribeService.java
    │              │  ├─📜SubscriptionScheduler.java
    │              │  ├─📜SubscriptionService.java
    │              │  ├─📜TMDBService.java
    │              │  ├─📜TranslationService.java
    │              │  ├─📜VideoProgressService.java
    │              │  ├─📜VideoService.java
    │              │  ├─📜VisitorCountService.java
    │              │  └─📜VisitorService.java
    │              └─📂util
    │                  └─📜YoutubeUrlConverter.java
    └─📂resources
        ├─📜application-oauth.properties
        ├─📜application.properties
        ├─📜service-account.json
        ├─📂static
        │  ├─📜google-logo.png
        │  ├─📜kakao-logo.png
        │  ├─📜naver-logo.png
        │  ├─📂css
        │  │  ├─📜itemDtl.css
        │  │  └─📜layout.css
        │  ├─📂img
        │  │  ├─📜12.jpg
        │  │  ├─📜322236_455901_4735.jpg
        │  │  ├─📜background.jpg
        │  │  ├─📜Bangers.png
        │  │  ├─📜btns.png
        │  │  ├─📜correction_btn.png
        │  │  ├─📜delete_btn.png
        │  │  ├─📜ex.png
        │  │  ├─📜left_btn.png
        │  │  ├─📜right_btn.png
        │  │  └─📜video_edit_btn.png
        │  ├─📂js
        │  └─📂video2
        │      ├─📜preview_sample.mp4
        │      └─📜video_sample.mp4
        └─📂templates
            ├─📜access-denied.html
            ├─📜admin-chat-list.html
            ├─📜chatbot.html
            ├─📜error-page.html
            ├─📜index.html
            ├─📜main.html
            ├─📜test.html
            ├─📂ad
            │  ├─📜adDtl.html
            │  ├─📜adForm.html
            │  └─📜adMng.html
            ├─📂admin
            │  ├─📜dashboard.html
            │  └─📂banner
            │      ├─📜bannerForm.html
            │      └─📜bannerList.html
            ├─📂error
            │  └─📜404.html
            ├─📂footer
            │  └─📜agree.html
            ├─📂fragments
            │  ├─📜footer.html
            │  └─📜header.html
            ├─📂home
            │  └─📜home.html
            ├─📂item
            │  ├─📜crawledItem.html
            │  ├─📜itemDtl.html
            │  ├─📜itemForm.html
            │  └─📜itemMng.html
            ├─📂layouts
            │  └─📜layout.html
            ├─📂legal
            │  └─📜legal-notice.html
            ├─📂members
            │  ├─📜find-email.html
            │  ├─📜find-password.html
            │  ├─📜memberForm.html
            │  ├─📜memberLoginForm.html
            │  ├─📜memberMyPage.html
            │  └─📜reset-password.html
            ├─📂order
            │  └─📜orderList.html
            ├─📂payment
            │  ├─📜failure.html
            │  ├─📜subscription.html
            │  └─📜success.html
            ├─📂subscribe
            │  ├─📜subscribeBuy.html
            │  └─📜subscribeForm.html
            └─📂video
                ├─📜play.html
                ├─📜playCrawl.html
                ├─📜videoForm.html
                └─📜videoMng.html



```
</details>



## 💾 기술 선정 이유


- **Spring Boot** :  빠르고 효율적인 애플리케이션 개발을 위해 사용하였으며, 자동 설정 및 내장 서버 지원을 통해 개발 및 배포 속도를 높이고, 마이크로서비스 아키텍처와의 뛰어난 호환성으로 확장성과 유지보수성을 강화하였다.

- **MySQL** : 안정적이고 고성능의 관계형 데이터베이스 관리 시스템(RDBMS)으로, 대규모 데이터 처리와 트랜잭션 관리에 적합하며, 높은 확장성과 다양한 플랫폼에서의 호환성 덕분에 신뢰성 있는 데이터 저장 솔루션을 제공한다.

- **websocket** : 실시간으로 소통할 수 있는 데이터를 주고 받을 수 있는 1:1 고객 상담 채팅 기능이 필요하다. 그러기 위해선 단방향 통신이 아닌 이벤트가 발생하는 즉시 데이터를 전송하는 구조인 양방향 통신을 지원하는 웹 소켓을 활용했다.



### API
- **PortOne** : 안전한 결제 시스템과 개인정보 보호 및 결제 정보 보안을 지키기 위해 사용하였으며, 사용자에게 다양한 결제 수단을 제공한다.

- **OAuth** : 사용자 비밀번호를 직접 처리하지 않고, 안전한 토큰 기반 인증을 제공하여 보안을 강화하고, 여러 서비스에서 중앙 집중식 인증을 통해 사용자 편의성을 높이며, 세분화된 권한 부여로 데이터 접근을 최소화하여 보안 리스크를 줄였다.

- **TMDB** : 사용자에게 최신 영화, TV 프로그램 및 관련 정보를 실시간으로 제공할 수 있도록 하였으며, 데이터베이스와 유연한 API를 통해 다양한 영화 정보를 손쉽게 조회하고, 사용자 맞춤형 콘텐츠 추천 및 검색 기능을 구현했다.

- **Google Cloud Translation API** : Google Cloud Translation API는 100개 이상의 언어를 지원하며, 기업용 API로 보안성이 뛰어나다. 일반적인 크롬에 있는 번역기능은 자동화 및 커스텀 번역이 불가능하기 때문에 API를 선택했다 
- **Apache POI** : chillstream에서는  장르별 분석과 인기 콘텐츠를 파악하여야 하기 때문에 엑셀 파일을 효율적으로 생성 및 관리할 수 있는 기능을 제공하는ApachePoI를 사용했다. 또한 Spring Boot와 잘 호환되기 때문에 선택했다.

### CI/CD
- **AWS** : 사용자가 필요에 따라 손쉽게 리소스를 확장하거나 축소할 수 있으며, 전 세계 여러 리전에 데이터 센터를 운영해 지역별로 높은 가용성을 유지할 수 있어 자연 재해나 데이터 센터 장애와 같은 상황에서도 비즈니스를 안전하게 보호할 수 있기 때문에 선택했다.

<br>


## 📌 트러블 슈팅

<details><summary>🧑🏻‍💻 현영민
</summary>

<br>
<br>

# 웹소켓 채팅 메시지 표시 문제 해결

## 📝 문제 설명
광고 재생 기능과 이어서 시청하기 기능이 개별적으로는 제대로 작동하였습니다. 그러나 두 기능을 동시에 적용하는 과정에서 이어서 시청하기 기능이 제대로 작동하지 않았습니다.

---

## 🔍 문제 발생 경과
- **이어서 시청하기 기능 구현**: 영상 시청기록이 있으면 재생 중단 부분부터 재생 가능
- **광고 재생 기능 구현**: 구독 등급에 따라 영상 시작 전 광고 영상 재생
- **두 기능을 동시에 적용**: 광고 영상이 끝난 후, 이어서 시청하기를 알리는 confirm이 나오지 않음

광고 영상 종료 함수 호출 후 이어서 시청하기 함수를 호출하였으나 같은 문제 발생


- 이어서 시청하기 기능 구현 – 영상 시청기록이 있으면 재생 중단 부분부터 재생 가능
광고 재생 기능 구현 – 구독 등급에 따라 영상 시작 전 광고 영상 재생
두 기능을 동시에 적용 – 광고 영상이 끝난 후, 이어서 시청하기를 알리는 confirm이 나오지 않음
광고 영상 종료 함수 호출 후 이어서 시청하기 함수를 호출하였으나 같은 문제 발생

---

## ⚠️ 원인
광고 종료 함수가 완전히 작동하기 전에 이어서 시청하기 함수가 호출되어 충돌이 발생한 것이라 짐작하였습니다.

---

## 🛠️ 해결 방법

**두 함수 사이에 setTimeout 함수로 지연 시간을 주어 충돌을 방지했습니다.**


---

## ✅ 결과
광고 영상이 끝난 후, ‘이어서 시청하시겠습니까?’ confirm이 정상적으로 출력되었습니다

---





## 


</details>

<details><summary>🧑🏻‍💻 최재영
</summary>

<br>


# 비밀번호 재설정 토큰 검증문제


## 📝 문제 설명
비밀번호 재설정 과정에서 가장 큰 문제는 토큰의 유효성 검사 및 관리였습니다.
비밀번호 재설정 링크는 이메일을 통해 사용자에게 전송되며, 이 링크에는 token이 포함되어 있습니다.

<br>

---


## 🔍 문제 발생 경과
- **토큰이 누락되거나, 클라이언트에서 올바르게 전달되지 않는 문제 :** 사용자가 비밀번호 재설정 페이지에 접근할 때, URL에서 totken을 추출해야한다. 하지만 토큰이 제대로 전달되지 않으면 서버에서 "유효하지 않은 토큰입니다."라는 오류가 발생했습니다.  
    

- **만료된 토큰을 사용한 경우**: `토큰은 일정 시간 이후 만료되도록 설정되어 있지만, 만료된 토큰을 사용했을 때 사용자에게 적절한 피드백을 제공해야 했습니다. 

- **서버에서 토큰 검증 시 예외 처리 부족 :** 서버에서 token을 조회할 때, 해당 토큰이 없거나 유효하지 않으면 적절한 오류 메시지를 반환해야 했습니다.
하지만 예외 처리가 미흡하여 정확한 원인을 사용자에게 안내하지 못하는 문제가 있었습니다.

<br>

---

## 🛠️  해결 방안 및 코드 설명



 <details><summary>클라이언트 측에서 토큰을 올바르게 전달
</summary>

```

    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    if (token) {
        document.getElementById('resetToken').value = token;
    }

```

1. 사용자가 비밀번호 재설정 페이지에 접근하면, URLSearchParams를 이용해 토큰을 자동으로 가져와 `<input type="hidden">`에 저장하도록 했습니다.
2. 이를 통해 사용자가 직접 토큰을 입력하지 않아도 서버로 올바른 요청을 보낼 수 있습니다.

<br>

</details>

<details><summary>서버에서 토큰 검증 및 오류 처리 강화
</summary>

```

 @GetMapping("/reset-password")
   public String showResetPasswordPage(@RequestParam("token") String token, Model model) {


      if (token == null || token.trim().isEmpty()) {
         throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
      }

      Member member = memberRepository.findByPasswordResetToken(token)
              .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

      if (member.getTokenExpiryDate() == null || member.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
         throw new IllegalArgumentException("토큰이 만료되었습니다.");
      }

      model.addAttribute("email", member.getEmail());
      model.addAttribute("token", token);
      return "members/reset-password";
   }


```


1. **토큰 유효성 검사 강화**

- token == null || token.trim().isEmpty()를 검사하여 빈 토큰을 방지했습니다.
- 토큰이 없으면 즉시 예외를 발생시켜 잘못된 요청을 처리합니다.

2. **DB에서 토큰 조회 시 예외 처리**

- .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."))
- 잘못된 토큰이 들어오면 IllegalArgumentException을 발생시켜 클라이언트에 오류 메시지를 반환합니다.

3. **토큰 만료 여부 확인**

- if (member.getTokenExpiryDate() == null || member.getTokenExpiryDate().isBefore(LocalDateTime.now()))
- 토큰 만료 시간이 현재 시각보다 이전이면 "토큰이 만료되었습니다."라는 오류를 반환하도록 설정했습니다.

<br>

</details>


<details><summary>토큰 검증 후 비밀번호 재설정 로직 개선
</summary>

```

@PostMapping("/reset-password")
   public ResponseEntity<String> resetPassword(
           @RequestParam("token") String token,
           @RequestParam("newPassword") String newPassword,
           @RequestParam("confirmPassword") String confirmPassword) {

      System.out.println("Received token: " + token);
      if (token == null || token.trim().isEmpty()) {
         return ResponseEntity.badRequest().body("토큰이 누락되었습니다.");
      }
      if (newPassword == null || newPassword.trim().isEmpty()) {
         return ResponseEntity.badRequest().body("새 비밀번호가 누락되었습니다.");
      }
      if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
         return ResponseEntity.badRequest().body("비밀번호 확인이 누락되었습니다.");
      }
      if (!newPassword.equals(confirmPassword)) {
         return ResponseEntity.badRequest().body("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
      }

      try {
         Member member = memberRepository.findByPasswordResetToken(token)
                 .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
         if (member.getTokenExpiryDate() == null || member.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("토큰이 만료되었습니다.");
         }

         member.setPassword(passwordEncoder.encode(newPassword));

         member.setPasswordResetToken(null);
         member.setTokenExpiryDate(null);
         memberRepository.save(member);

         return ResponseEntity.ok("비밀번호가 성공적으로 재설정되었습니다.");

      } catch (IllegalArgumentException e) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      } catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
      }
   }

```

1. **토큰 누락 방지**

- if (token == null || token.trim().isEmpty()) 조건을 추가하여, 클라이언트가 token을 보내지 않았을 때 즉시 오류를 반환하도록 했습니다.
2. **비밀번호 확인 검사**

- if (!newPassword.equals(confirmPassword))
- 사용자가 입력한 두 비밀번호가 다르면 오류 메시지를 반환하도록 설정했습니다.
3. **토큰이 만료되었는지 확인**

- if (member.getTokenExpiryDate().isBefore(LocalDateTime.now()))
-토큰이 만료된 경우 "토큰이 만료되었습니다." 메시지를 반환하도록 설정했습니다.
4. **토큰 초기화**

- member.setPasswordResetToken(null);
- member.setTokenExpiryDate(null);
- 비밀번호가 재설정된 후, 토큰을 제거하여 동일한 토큰을 재사용할 수 없도록 했습니다.

<br>
<br>

---

</details>


## ✅결론

**비밀번호 재설정 과정에서 발생했던 토큰 유효성 검사, 만료 처리, 오류 메시지 반환 등의 문제를 해결하기 위해 다음과 같은 개선을 수행했습니다.**
- 클라이언트 측에서 URL 토큰을 자동으로 추출하여 전송
- 서버 측에서 토큰이 없거나 유효하지 않은 경우 상세한 오류 메시지 반환
- 비밀번호 검증 및 토큰 만료 검사 추가
- 비밀번호 변경 후 토큰을 제거하여 보안 강화

이러한 개선을 통해 보다 안전하고 사용자 친화적인 비밀번호 재설정 시스템을 구현할 수 있었습니다. 

---





## 


</details>

<details><summary>👩💻 김민주
</summary>

<br>
<br>

# 웹소켓 채팅 메시지 표시 문제 해결

## 📝 문제 설명
채팅 기능을 구현하는 중에 웹소켓을 통해 메시지가 서버에서 정상적으로 전송되고 있었으나, 클라이언트 화면에는 메시지가 표시되지 않는 문제가 발생했습니다. 콘솔에서는 메시지가 정상적으로 출력되었지만, 사용자 인터페이스(UI)에는 메시지가 나타나지 않았습니다.

---

## 🔍 문제 발생 경과
- **서버에서 메시지 전송**: 메시지가 서버에서 정상적으로 전송됨.
- **클라이언트 구독 설정**: `stompClient.subscribe()`를 통해 `/topic/{roomId}`에 대한 구독을 설정.
- **메시지 수신 확인**: 클라이언트는 메시지를 정상적으로 수신했으나, 화면에 표시되지 않음.
- **문제 지속**: 웹소켓 연결 및 메시지 전송은 정상적으로 이루어졌으나, 화면에 메시지가 나타나지 않음.

---

## ⚠️ 원인
이 문제의 원인은 **웹소켓 구독 경로에 오타**가 있었기 때문입니다.  
구독 경로인 `/topic/{roomId}`에서 `roomId`가 정확히 매칭되지 않으면, 서버에서 보낸 메시지가 클라이언트에서 올바르게 수신되지 않아 화면에 표시되지 않았습니다.

---

## 🛠️ 해결 방법
1. **클라이언트 코드 수정**: 구독 경로인 `/topic/{roomId}`를 정확하게 수정하여, 서버에서 해당 경로로 메시지를 전송할 수 있도록 조정.
2. **서버 설정 점검**: 서버의 `WebSocketConfig` 설정을 확인하여 `/topic`을 사용하는 메시지 브로커가 정상적으로 활성화되어 있는지 검토.
3. **테스트 후 확인**: 구독 경로 수정 후 다시 테스트하여, 메시지가 정상적으로 클라이언트 화면에 표시되도록 함.

---

## ✅ 결과
이 문제를 해결한 후, 웹소켓을 통해 전송된 채팅 메시지가 클라이언트 화면에 정상적으로 표시되었습니다.

---





## 


</details>




