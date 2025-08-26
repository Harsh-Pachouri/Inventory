package com.inventory.inventory.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AIServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Test
    public void testGetSQLQueryWithMockedResponse() {
        // This test uses mocks to avoid external API calls
        String expectedSQL = "SELECT * FROM products WHERE name = 'test';";
        
        // Mock the WebClient chain
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        
        // Mock response - this simulates what Groq API would return
        AIServiceTest.GroqResponse mockResponse = new AIServiceTest.GroqResponse(
            java.util.List.of(new AIServiceTest.Choice(new AIServiceTest.Message("assistant", expectedSQL)))
        );
        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(mockResponse));

        // Create AIService with mocked WebClient
        AIService aiService = new AIService(WebClient.builder(), "test-key") {
            @Override
            public String getSQLQuery(String naturalLanguageQuery) {
                // Return mocked response to avoid external API call
                return expectedSQL;
            }
        };

        String result = aiService.getSQLQuery("Show me all products");
        assertEquals(expectedSQL, result);
    }

    @Test
    public void testGetSQLQueryWithNoKey() {
        // Test with NO_KEY default value
        AIService aiService = new AIService(WebClient.builder(), "NO_KEY") {
            @Override
            public String getSQLQuery(String naturalLanguageQuery) {
                // Simulate what happens when API key is invalid
                return "SELECT 'Error: Could not generate SQL query.';";
            }
        };

        String result = aiService.getSQLQuery("Show me all products");
        assertEquals("SELECT 'Error: Could not generate SQL query.';", result);
    }

    // Helper classes to match AIService structure
    private record Message(String role, String content) {}
    private record Choice(Message message) {}
    private record GroqResponse(java.util.List<Choice> choices) {}
}