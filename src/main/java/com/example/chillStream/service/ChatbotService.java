package com.example.chillStream.service;

import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

@Service
public class ChatbotService {
    
    private final String[] greetings = {
        "안녕하세요! 오늘도 좋은 하루 보내세요 😊",
        "반갑습니다! 무엇을 도와드릴까요?",
        "어서오세요! ChillStream입니다.",
        "안녕하세요! 즐거운 시청 되세요~",
        "반가워요! 어떤 콘텐츠를 찾으시나요?"
    };

    public String getRandomGreeting() {
        Random random = new Random();
        return greetings[random.nextInt(greetings.length)];
    }

    public Map<String, Object> processQuery(String question) {
        question = question.replaceAll("[^a-zA-Z0-9가-힣\\s]", "")
                          .toLowerCase()
                          .trim();
                          
        Map<String, Object> response = new HashMap<>();
        
        // 인사 관련 질문
        if (question.contains("안녕") || question.contains("하이") || question.contains("반가워")) {
            response.put("type", "faq");
            response.put("answer", getRandomGreeting());
            return response;
        }
        
        // 시청 방법 관련 질문
        if (question.contains("어떻게") && (question.contains("보나") || question.contains("시청"))) {
            response.put("type", "faq");
            response.put("answer", "PC, 모바일, 태블릿 등 다양한 기기에서 시청하실 수 있습니다. 로그인 후 원하시는 콘텐츠를 선택하여 시청하시면 됩니다.");
            return response;
        }
        
        // 요금 관련 질문
        if (question.contains("요금") || question.contains("가격") || question.contains("얼마") || question.contains("구독료")) {
            response.put("type", "faq");
            response.put("answer", "월 5,500원부터 17,000원까지 다양한 요금제를 제공하고 있습니다. 자세한 내용은 요금제 페이지를 확인해주세요.");
            return response;
        }
        
        // 콘텐츠 관련 질문
        if (question.contains("컨텐츠") || question.contains("콘텐츠") || question.contains("뭐") || question.contains("어떤")) {
            response.put("type", "faq");
            response.put("answer", "영화, 드라마, 예능, 다큐멘터리 등 다양한 장르의 콘텐츠를 제공하고 있습니다. 메인 페이지에서 추천 콘텐츠를 확인해보세요!");
            return response;
        }
        
        // 로그인 관련 질문
        if (question.contains("로그인") || question.contains("가입")) {
            response.put("type", "faq");
            response.put("answer", "우측 상단의 로그인 버튼을 클릭하여 로그인하실 수 있습니다. 아직 회원이 아니시라면 회원가입을 먼저 진행해주세요.");
            return response;
        }
        
        // 기본 응답
        response.put("type", "redirect");
        response.put("message", "죄송합니다. 질문을 이해하지 못했습니다. 더 자세한 내용은 1:1 채팅으로 문의해주세요.");
        
        return response;
    }
} 