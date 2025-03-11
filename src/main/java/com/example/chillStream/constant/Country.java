package com.example.chillStream.constant;

public enum Country {
    KOREA("한국"),
    USA("미국"),
    EUROPE("유럽"),
    JAPAN("일본"),
    CHINA("중국");
    
    private final String displayName;
    
    Country(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
