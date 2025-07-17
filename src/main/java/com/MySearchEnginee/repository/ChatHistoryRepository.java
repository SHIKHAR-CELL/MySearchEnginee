package com.MySearchEnginee.repository;

import com.MySearchEnginee.Entities.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
	
}
