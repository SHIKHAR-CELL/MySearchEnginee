package com.MySearchEnginee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.MySearchEnginee.service.ChatHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@RestController
@RequestMapping("/api/deepseek")
public class deepseekcontroller {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;
    
    @Autowired
    private ChatHistoryService historyService;

    
    
    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String prompt) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(apiUrl);

            // Prepare the request body for OpenRouter
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", "deepseek/deepseek-r1-0528:free");
 // ✅ Correct


            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            payload.put("messages", messages);

            ObjectMapper mapper = new ObjectMapper();
            StringEntity entity = new StringEntity(mapper.writeValueAsString(payload));
            post.setEntity(entity);

            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + apiKey);

            ClassicHttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    String finalResponse = result.toString();
                    historyService.save("deepseek", prompt, finalResponse);
                    return ResponseEntity.ok(finalResponse);

                }
            } else {
                return ResponseEntity.status(500).body("No response from OpenRouter");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
