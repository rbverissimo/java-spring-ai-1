package com.coltran.javaspringai1.presentation.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class AgentController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    
    public AgentController(ChatClient.Builder chatClientBuilder, 
        @Qualifier("windowChatMemory") ChatMemory chatMemory, 
        VectorStore vectorStore) {
        this.vectorStore = vectorStore;
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

        List<Document> similarDocs = vectorStore.similaritySearch(
            SearchRequest.builder().query(query).topK(4).build()
        );

        String documentContext = similarDocs.stream()
            .map(Document::getFormattedContent)
            .collect(Collectors.joining("\n\n---\n\n"));

        return chatClient.prompt()
            .system(sp -> sp.text(systemRule).param("context", documentContext))
            .toolNames("checkServiceHealth")
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, chatId))
            .user(query)
            .call()
            .content();
    }
}
