package com.medeat.chatbot.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.medeat.chatbot.dto.GmsResponse;
import com.medeat.chatbot.service.ChatbotService;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final WebClient gmsWebClient;

    @Value("${gms.model:gpt-4.1}")
    private String model;

    // ✅ Lombok 없이 명시적 생성자
    public ChatbotServiceImpl(WebClient gmsWebClient) {
        this.gmsWebClient = gmsWebClient;
    }

    @Override
    public String ask(String message) {

        // 1️⃣ 요청 바디 구성
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("input", message);

        // 2️⃣ GMS 호출
        GmsResponse response = gmsWebClient.post()
                .uri("/api.openai.com/v1/responses")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(GmsResponse.class)
                .block();

        // 3️⃣ 방어 코드
        if (response == null
                || response.getOutput() == null
                || response.getOutput().isEmpty()
                || response.getOutput().get(0).getContent() == null
                || response.getOutput().get(0).getContent().isEmpty()) {
            return "AI 응답을 가져오지 못했습니다.";
        }

        // 4️⃣ 실제 텍스트만 추출
        String answer = response
                .getOutput()
                .get(0)
                .getContent()
                .get(0)
                .getText();

        return answer != null ? answer : "AI 응답이 비어 있습니다.";
    }
}
