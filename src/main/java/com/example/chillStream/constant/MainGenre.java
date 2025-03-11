package com.example.chillStream.constant;

public enum MainGenre {
    DRAMA("드라마"),
    MOVIE("영화"),
    ANIMATION("애니메이션"),
    ENTERTAINMENT("예능");
    
    private final String displayName;
    
    
    MainGenre(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName(){
        return displayName;
    }
}