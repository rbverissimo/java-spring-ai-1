package com.coltran.javaspringai1.presentation.controllers;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.RestController;
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
    
    public AgentController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/ask")
    public String askAgent(@RequestParam @NotNull String query) {
        String systemRule = """
                    You are a helpful assistant.
                    Answer the user's question strictly using the provided context below.
                    If the answer is not in the context, say "I don't know".
                    
                    CONTEXT:
                    {context}        
                """;

        PromptTemplate promptTemplate = new PromptTemplate(systemRule);
        Prompt prompt = promptTemplate.create(Map.of("context", HARDCODED_DOC));
        return chatClient.prompt(prompt)
            .user(query)
            .call()
            .content();
    }
}
