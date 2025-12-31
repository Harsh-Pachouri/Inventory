package com.inventory.inventory.api.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AIService(WebClient.Builder webClientBuilder,
                     ObjectMapper objectMapper,
                     @Value("${GROQ_API_KEY:NO_KEY}") String groqApiKey) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String getSQLQuery(String naturalLanguageQuery) {
        // 1. SYSTEM PROMPT: Strict JSON Output
        String systemPrompt = """
            You are an inventory database assistant.
            Schema: product (id, name, quantity, price, supplier_id), supplier (id, name).
            
            Analyze the user's input and return a JSON object with two fields: "type" and "content".
            
            1. If the user asks for DATA (e.g., "how many items?", "list products"), generate a Postgres SQL query.
               Format: { "type": "SQL", "content": "SELECT..." }
            
            2. If the user CHATS (e.g., "hi", "thanks", "who are you?"), write a helpful response.
               Format: { "type": "CHAT", "content": "Hello! I am your Inventory AI..." }
               
            3. If the request is UNRELATED (e.g., "weather", "jokes"), refuse politely.
               Format: { "type": "CHAT", "content": "I can only answer inventory questions." }
            
            IMPORTANT: Return ONLY the raw JSON. No markdown. No explanations.
            """;

        // 2. Request Payload using YOUR working model
        GroqRequest requestPayload = new GroqRequest(
                "llama-3.3-70b-versatile", // <--- LOCKED IN CORRECT MODEL
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", naturalLanguageQuery)
                ),
                Map.of("type", "json_object") // Enforce JSON mode
        );

        try {
            String jsonBody = objectMapper.writeValueAsString(requestPayload);
            logger.info("Sending to AI: {}", jsonBody);

            GroqResponse response = this.webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(jsonBody)
                    .retrieve()
                    .bodyToMono(GroqResponse.class)
                    .block();

            if (response != null && !response.choices().isEmpty()) {
                String rawContent = response.choices().get(0).message().content().trim();

                // Cleanup Markdown if AI adds it (defensive coding)
                if (rawContent.startsWith("```json")) {
                    rawContent = rawContent.replace("```json", "").replace("```", "").trim();
                } else if (rawContent.startsWith("```")) {
                    rawContent = rawContent.replace("```", "").trim();
                }

                logger.info("AI Response: {}", rawContent);

                // 3. PARSE JSON & DECIDE
                JsonNode rootNode = objectMapper.readTree(rawContent);
                String type = rootNode.path("type").asText().toUpperCase();
                String content = rootNode.path("content").asText();

                if ("SQL".equals(type)) {
                    // It's a query -> Return it directly so the DB executes it
                    return content;
                } else {
                    // It's Chat/Error -> Wrap it in Safe SQL so the Controller doesn't crash
                    // We escape single quotes to prevent SQL syntax errors
                    String safeMessage = content.replace("'", "''");
                    return "SELECT '" + safeMessage + "' as message;";
                }
            }
        } catch (WebClientResponseException e) {
            logger.error("Groq API Error: {}", e.getResponseBodyAsString());
            return "SELECT 'Error: AI Provider unavailable.' as error;";
        } catch (Exception e) {
            logger.error("AI Internal Error: ", e);
            return "SELECT 'Error: System failure.' as error;";
        }

        return "SELECT 'Error: No response.' as error;";
    }

    // --- Records (Public & Annotated) ---
    public record Message(
            @JsonProperty("role") String role,
            @JsonProperty("content") String content
    ) {}

    public record GroqRequest(
            @JsonProperty("model") String model,
            @JsonProperty("messages") List<Message> messages,
            @JsonProperty("response_format") Object responseFormat
    ) {}

    public record Choice(
            @JsonProperty("message") Message message
    ) {}

    public record GroqResponse(
            @JsonProperty("choices") List<Choice> choices
    ) {}
}