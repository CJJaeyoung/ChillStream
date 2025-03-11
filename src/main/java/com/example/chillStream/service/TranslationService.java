package com.example.chillStream.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationService {

    private final Translate translate;

    public String translateText(String text, String targetLanguage) {
        try {
            if (text == null || text.isEmpty()) {
                return "";
            }
            
            Translation translation = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage("ko"),
                Translate.TranslateOption.targetLanguage(targetLanguage)
            );
            
            return translation.getTranslatedText();
        } catch (Exception e) {
            log.error("Translation error: ", e);
            throw new RuntimeException("번역 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 