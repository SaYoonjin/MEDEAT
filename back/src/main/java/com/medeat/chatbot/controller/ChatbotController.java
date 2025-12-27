package com.medeat.chatbot.controller;

import com.medeat.chatbot.dto.ChatRequest;
import com.medeat.chatbot.dto.ChatResponse;
import com.medeat.chatbot.service.ChatbotService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;
    
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req) {
        String answer = chatbotService.ask(req.getMessage());
        return new ChatResponse(answer);
    }
}
