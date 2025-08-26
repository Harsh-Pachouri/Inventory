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
        String systemPrompt = '''You are an SQL expert with expertise in PostgreSQL. 
            Database Schema (MANDATORY - DO NOT MODIFY):
            The following tables exist and were created using these exact commands:
            1) create table product (id bigserial primary key, name varchar(255) not null, quantity int not null, price double precision not null);
            2) create table supplier (id bigserial primary key, name varchar(255) not null);
            3) alter table product add column supplier_id bigint, add constraint FKsupplierID foreign key (supplier_id) references supplier (id);

            ## Task Requirements (STRICT COMPLIANCE REQUIRED):
            Convert the users natural language request into a PostgreSQL-compatible SQL query.

            ## Response Format (NON-NEGOTIABLE):
                - Your response MUST contain ONLY the SQL query
                - NO explanations, comments, markdown formatting, or additional text
                - NO code blocks (```) or formatting
                - The response must be executable directly in PostgreSQL without modification
                - Use proper PostgreSQL syntax and data types as defined in the schema above
                - Column names and table names must match the schema exactly (case-sensitive)

            ## Validation Checklist:
                - [ ] Query uses only tables: product, supplier
                - [ ] Column references match schema exactly: product(id, name, quantity, price, supplier_id), supplier(id, name)
                - [ ] Foreign key relationship respected: product.supplier_id -> supplier.id
                - [ ] PostgreSQL-specific syntax used where applicable
                - [ ] No extra text, formatting, or explanations included

                CRITICAL: Any response containing anything other than a pure SQL query will be rejected.
            '''
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

    public boolean isValid(String sql){
        if(sql.substring(0,7)=="select")return true
        return false;
    }
    private record Message(String role, String content) {}
    private record GroqRequest(String model, List<Message> messages) {}
    private record Choice(Message message) {}
    private record GroqResponse(List<Choice> choices) {}
}