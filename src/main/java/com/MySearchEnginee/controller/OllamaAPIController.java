package com.MySearchEnginee.controller;

import java.util.Map;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.MySearchEnginee.service.ChatHistoryService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class OllamaAPIController {

    @Autowired
    private OllamaChatModel chatModel;

    @Autowired
    private ChatHistoryService historyService;

    // ✅ Plain response for frontend API calls like: /ai/generate?message=...
    @GetMapping("/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        String response = this.chatModel.call(message);
        historyService.save("ollama", message, response); // Save to DB
        return Map.of("generation", response);
    }

    // ✅ Streaming version (if you're using streaming somewhere)
    @GetMapping("/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }
}
