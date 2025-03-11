package com.example.chillStream.constant;

public enum SubGenre {
    FANTASY("판타지"),        // 판타지 (14)
    SF("SF"),            // SF (878)
    DRAMA("드라마"),         // 드라마 (18)
    ROMANCE("로맨스"),       // 로맨스 (10749)
    COMEDY("코미디"),        // 코미디 (35)
    ACTION("액션"),        // 액션 (28)
    THRILLER("스릴러"),      // 스릴러 (53)
    HISTORY("역사"),       // 역사 (36)
    HORROR("공포"),        // 공포 (27)
    MYSTERY("미스터리"),       // 미스터리 (9648)
    CRIME("범죄"),         // 범죄 (80)
    ADVENTURE("모험"),     // 모험 (12)
    FAMILY("가족"),        // 가족 (10751)
    WAR("전쟁"),           // 전쟁 (10752)
    WESTERN("서부"),       // 서부 (37)
    DOCUMENTARY("다큐멘터리"),   // 다큐멘터리 (99)
    MUSIC("음악"),         // 음악 (10402)
    ANIMATION("애니메이션"),     // 애니메이션 (16)
    VARIETY("예능"),       // 예능 (10764)
    ETC("기타");            // 기타
    
    private final String displayName;
    
    SubGenre(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName(){
        return displayName;
    }
}
