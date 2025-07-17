package com.MySearchEnginee.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String modelUsed;
    private String prompt;

    @Column(length = 5000)
    private String response;

    private LocalDateTime timestamp = LocalDateTime.now();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModelUsed() {
		return modelUsed;
	}

	public void setModelUsed(String modelUsed) {
		this.modelUsed = modelUsed;
	}

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "ChatHistory [id=" + id + ", modelUsed=" + modelUsed + ", prompt=" + prompt + ", response=" + response
				+ ", timestamp=" + timestamp + "]";
	}

	public ChatHistory(Long id, String modelUsed, String prompt, String response, LocalDateTime timestamp) {
		super();
		this.id = id;
		this.modelUsed = modelUsed;
		this.prompt = prompt;
		this.response = response;
		this.timestamp = timestamp;
	}

	public ChatHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

    
}
