package com.coltran.javaspringai1.presentation.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.ChatModelCallAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class AgentController {

    private final ChatClient chatClient;

    private static final String HARDCODED_DOC = """
                CONFIDENTIAL PROJECT 'TITAN':
                Project Titan is a new microservices initiative to replace the legacy Monolith 'Zeus'.
                The deadline for Phase 1 is December 2025.
                The tech stack must be Java 21, Spring Boot 3.4, and PostgreSQL.
                The lead architect is Robert V.
                Cost Constraints: No AWS Lambda functions allowed, strictly Kubernetes on EC2.
            """;
    
    public AgentController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())    
        .build();
    }

    @GetMapping("/ask")
    public String askAgent(@RequestParam @NotNull String query,
        @RequestParam(defaultValue = "default-session") String chatId
    ) {
        String systemRule = """
                    You are a helpful assistant.
                    Answer the user's question strictly using the provided context below.
                    If the answer is not in the context, say "I don't know".
                    
                    CONTEXT:
                    {context}        
                """;

        return chatClient.prompt()
            .system(sp -> sp.text(systemRule).param("context", HARDCODED_DOC))
            .toolNames("checkServiceHealth")
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
            .user(query)
            .call()
            .content();
    }
}
