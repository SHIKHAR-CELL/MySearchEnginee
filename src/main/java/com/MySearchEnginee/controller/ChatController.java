package com.MySearchEnginee.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.HttpEntity;

import com.MySearchEnginee.Entities.ChatHistory;
import com.MySearchEnginee.repository.ChatHistoryRepository;
import com.MySearchEnginee.service.ChatHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatHistoryRepository historyRepository;

    @Autowired
    private ChatHistoryService historyService;

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @GetMapping("/converse")
    public String showChat(Model model) {
        List<ChatHistory> messages = historyRepository.findAll();
        messages.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())); // latest first
        model.addAttribute("historyList", messages);
        model.addAttribute("newPrompt", "");
        return "chat-window";
    }
    
//    @GetMapping("/ai/generate")
//    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//        String response = this.chatModel.call(message);
//        historyService.save("ollama", message, response); // Save to DB
//        return Map.of("generation", response);
//    }
    
//    @GetMapping("/ai/generateStream")
//  	public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
//          Prompt prompt = new Prompt(new UserMessage(message));
//          return this.chatModel.stream(prompt);
//      }
    
    @PostMapping("/ask")
    public String askQuestion(@RequestParam("newPrompt") String prompt,
                              @RequestParam("model") String modelChoice,
                              Model modelView) {
        String response = "";

        try {
            if ("ollama".equalsIgnoreCase(modelChoice)) {
                response = ollamaChatModel.call(prompt);
                historyService.save("ollama", prompt, response);
            } else if ("deepseek".equalsIgnoreCase(modelChoice)) {
                response = callDeepSeekApi(prompt);
                historyService.save("deepseek", prompt, response);
            } else {
                response = "Unknown model selected.";
            }
        } catch (Exception e) {
            response = "Error: " + e.getMessage();
        }

        return "redirect:/chat/converse"; // refresh chat
    }

    private String callDeepSeekApi(String prompt) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(apiUrl);

            // Payload setup
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "deepseek/deepseek-r1-0528:free");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            payload.put("messages", messages);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);
            post.setEntity(new StringEntity(json));

            // Headers
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + apiKey);

            // Use try-with-resources for response
            try (var response = client.execute(post)) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        return result.toString();
                    }
                } else {
                    return "No response content from DeepSeek.";
                }
            }
        } catch (Exception e) {
            return "Error while calling DeepSeek: " + e.getMessage();
        }
    }
    
   
        
    
}
