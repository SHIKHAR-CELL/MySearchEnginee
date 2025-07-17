package com.MySearchEnginee.service;

import com.MySearchEnginee.Entities.ChatHistory;
import com.MySearchEnginee.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatHistoryService {

    @Autowired
    private ChatHistoryRepository repository;

    public void save(String modelUsed, String prompt, String response) {
        ChatHistory history = new ChatHistory();
        history.setModelUsed(modelUsed);
        history.setPrompt(prompt);
        history.setResponse(response);
        repository.save(history);
    }
}
