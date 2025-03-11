package com.example.chillStream.constant;

public enum Subscription {
   INACTIVE("비활성중"),
   BASIC("베이직 구독"),
   PREMIUM("프리미엄 구독");
   
   private final String displayName;
   
   Subscription(String displayName) {
      this.displayName = displayName;
   }
   
   public String getDisplayName() {
      return displayName;
   }
}

