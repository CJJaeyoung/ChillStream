package com.example.chillStream.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {
    
    @GetMapping("/legal-notice")
    public String legalNotice() {
        return "legal/legal-notice";
    }
} 