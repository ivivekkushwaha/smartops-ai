
package com.smartops.insightservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartops.common.event.AlertEvent;
import com.smartops.insightservice.model.Insight;
import com.smartops.insightservice.repository.InsightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsightService {

    private final InsightRepository repository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public Insight saveInsight(AlertEvent event) {

        String prompt = """
                You are a Site Reliability Engineer.

                Analyze the following alert.

                Service: %s
                Severity: %s
                Title: %s
                Message: %s

                Return ONLY valid JSON.
                Do not use Markdown code blocks.

                {
                  "severity": "CRITICAL",
                  "summary": "Brief incident summary",
                  "rootCause": "Possible root cause",
                  "impact": "Impact on users or system",
                  "recommendation": "Recommended actions",
                  "confidence": 85
                }
                """.formatted(
                event.getServiceName(),
                event.getSeverity(),
                event.getTitle(),
                event.getMessage()
        );

        try {
            // Step 1: Get response from Gemini
            String rawResponse = geminiService.generateInsight(prompt);

            // Step 2: Extract generated text from Gemini API response
            String generatedText = extractGeneratedText(rawResponse);

            // Step 3: Parse Gemini-generated JSON
            JsonNode aiResult = parseAiResponse(generatedText);

            // Step 4: Save individual fields
            Insight insight = Insight.builder()
                    .userId(event.getUserId())
                    .serviceName(event.getServiceName())
                    .severity(event.getSeverity())
                    .summary(getText(aiResult, "summary", "AI analysis unavailable"))
                    .rootCause(getText(aiResult, "rootCause", "Not identified"))
                    .impact(getText(aiResult, "impact", "Not specified"))
                    .recommendation(getText(aiResult, "recommendation", "Not available"))
                    .confidence(aiResult.path("confidence").asInt(0))
                    .createdAt(LocalDateTime.now())
                    .build();

            return repository.save(insight);

        } catch (Exception e) {

            log.error("Failed to generate or parse AI insight", e);

            Insight fallbackInsight = Insight.builder()
                    .userId(event.getUserId())
                    .serviceName(event.getServiceName())
                    .severity(event.getSeverity())
                    .summary("AI analysis could not be generated")
                    .rootCause("Unable to determine")
                    .impact("AI analysis unavailable")
                    .recommendation("Please retry the analysis later")
                    .confidence(0)
                    .createdAt(LocalDateTime.now())
                    .build();

            return repository.save(fallbackInsight);
        }
    }

    private String extractGeneratedText(String rawResponse) throws Exception {

        JsonNode response = objectMapper.readTree(rawResponse);

        // Gemini API response structure:
        // candidates -> content -> parts -> text

        JsonNode textNode = response
                .path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (!textNode.isMissingNode() && !textNode.isNull()) {
            return textNode.asText();
        }

        // Also support a direct JSON response
        return rawResponse;
    }

    private JsonNode parseAiResponse(String generatedText) throws Exception {

        String cleanedResponse = generatedText.trim();

        // Remove Markdown JSON code fences if Gemini returns them
        if (cleanedResponse.startsWith("```")) {
            cleanedResponse = cleanedResponse
                    .replaceFirst("^```(?:json)?\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
        }

        return objectMapper.readTree(cleanedResponse);
    }

    private String getText(JsonNode node, String field, String fallback) {

        JsonNode value = node.path(field);

        if (value.isMissingNode() || value.isNull() || value.asText().isBlank()) {
            return fallback;
        }

        return value.asText();
    }

    public List<Insight> getAllInsights(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Insight getInsightById(String id, String userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new RuntimeException("Insight not found with id: " + id));
    }

    public void deleteInsight(String id, String userId) {
        Insight insight = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Insight not found"
                ));

        repository.delete(insight);
    }
    public void deleteAllInsights(String userId) {
        repository.deleteByUserId(userId);
    }

    public long getInsightCount(String userId) {
        return repository.countByUserId(userId);
    }
    public void deleteInsightsByService(String serviceName, String userId) {
        repository.deleteByUserIdAndServiceName(userId, serviceName);
    }
}
