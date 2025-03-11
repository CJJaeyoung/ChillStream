package com.example.chillStream.util;

public class YoutubeUrlConverter {
    
    public static String convertToEmbedUrl(String youtubeUrl) {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            return null;
        }
        
        // YouTube URL에서 비디오 ID 추출
        String videoId = null;
        if (youtubeUrl.contains("watch?v=")) {
            videoId = youtubeUrl.split("watch\\?v=")[1];
        } else if (youtubeUrl.contains("youtu.be/")) {
            videoId = youtubeUrl.split("youtu\\.be/")[1];
        }
        
        // 파라미터가 있는 경우 제거
        if (videoId != null && videoId.contains("&")) {
            videoId = videoId.split("&")[0];
        }
        
        // 임베드 URL 생성
        return videoId != null ? "https://www.youtube.com/embed/" + videoId : null;
    }
} 