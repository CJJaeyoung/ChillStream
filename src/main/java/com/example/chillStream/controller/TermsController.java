package com.example.chillStream.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TermsController {
@GetMapping("/agree")
    public String terms(){
    return "/footer/agree";
}
}
