package com.example.chillStream.controller;

import com.example.chillStream.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping("/translate")
    public ResponseEntity<String> translate(@RequestBody TranslationRequest request) {
        String translatedText = translationService.translateText(
            request.getText(), 
            request.getTargetLanguage()
        );
        return ResponseEntity.ok(translatedText);
    }
}

class TranslationRequest {
    private String text;
    private String targetLanguage;
    
    // Getter와 Setter
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getTargetLanguage() {
        return targetLanguage;
    }
    
    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
} 