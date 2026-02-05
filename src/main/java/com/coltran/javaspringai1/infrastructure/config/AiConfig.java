package com.coltran.javaspringai1.infrastructure.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemory windowChatMemory(JdbcTemplate jdbcTemplate){

        var repository = JdbcChatMemoryRepository
            .builder()
            .jdbcTemplate(jdbcTemplate)
            .build();

        return MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(repository)
            .maxMessages(10)
            .build();
    }
}
