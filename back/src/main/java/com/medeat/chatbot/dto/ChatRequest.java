package com.medeat.chatbot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatRequest {
    private String message;
    
    public ChatRequest() {
		// TODO Auto-generated constructor stub
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
    
}
