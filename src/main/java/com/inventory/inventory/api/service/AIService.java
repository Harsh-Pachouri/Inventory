package com.inventory.inventory.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AIService {

    private final WebClient webClient;

    public AIService(WebClient.Builder webClientBuilder, @Value("${GROQ_API_KEY:NO_KEY}") String groqApiKey) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.groq.com/openai/v1") // Groq API base URL
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String getSQLQuery(String naturalLanguageQuery) {
        String systemPrompt = "You are a SQL expert. Convert the following natural language question into a SQL query.";

        GroqRequest requestPayload = new GroqRequest(
                "llama3-8b-8192",
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", naturalLanguageQuery)
                )
        );

        GroqResponse response = this.webClient.post()
                .uri("/chat/completions")
                .body(Mono.just(requestPayload), GroqRequest.class)
                .retrieve()
                .bodyToMono(GroqResponse.class)
                .block();

        if (response != null && !response.choices().isEmpty()) {
            return response.choices().get(0).message().content();
        }

        return "SELECT 'Error: Could not generate SQL query.';";
    }

    private record Message(String role, String content) {}
    private record GroqRequest(String model, List<Message> messages) {}
    private record Choice(Message message) {}
    private record GroqResponse(List<Choice> choices) {}
}