
package com.smartops.insightservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.Map;



@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String generateInsight(String prompt) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Gemini API key is missing");
        }

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt cannot be empty");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        try {
            JsonNode response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("generativelanguage.googleapis.com")
                            .path("/v1beta/models/gemini-3.5-flash-lite:generateContent")
                            .queryParam("key", apiKey)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block(Duration.ofSeconds(30));

            JsonNode textNode = response == null
                    ? null
                    : response.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text");

            if (textNode == null || textNode.isMissingNode()
                    || textNode.isNull() || textNode.asText().isBlank()) {
                throw new IllegalStateException(
                        "Gemini returned an empty response: " + response
                );
            }

            return textNode.asText();

        } catch (WebClientResponseException e) {

            log.error("Gemini API error: status={}, response={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString());

            throw new IllegalStateException(
                    "Gemini API request failed: " + e.getResponseBodyAsString(), e
            );
        }
    }
}
