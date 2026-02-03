package com.coltran.javaspringai1.infrastructure.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(new InMemoryChatMemoryRepository())
            .build();
    }
}
