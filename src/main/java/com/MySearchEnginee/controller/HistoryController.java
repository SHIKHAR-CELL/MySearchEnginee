package com.MySearchEnginee.controller;

import com.MySearchEnginee.Entities.ChatHistory;
import com.MySearchEnginee.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat/history")
public class HistoryController {

    @Autowired
    private ChatHistoryRepository historyRepository;

    // Show all history
    @GetMapping("/view")
    public String viewHistory(Model model) {
        List<ChatHistory> historyList = historyRepository.findAll();
        model.addAttribute("historyList", historyList);
        return "chat-history"; // Renders chat-history.html
    }

    // Handle deletion of a specific entry
    @PostMapping("/delete/{id}")
    public String deleteHistoryEntry(@PathVariable Long id) {
        historyRepository.deleteById(id);
        return "redirect:/chat/history/view"; // reload page after delete
    }
}
