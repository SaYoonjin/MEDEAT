package com.medeat.chatbot.controller;

import com.medeat.chatbot.dto.ChatRequest;
import com.medeat.chatbot.dto.ChatResponse;
import com.medeat.chatbot.service.ChatbotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req) {
        return new ChatResponse(chatbotService.ask(req.getMessage()));
    }
}
