
package com.smartops.insightservice.controller;

import com.smartops.insightservice.model.Insight;
import com.smartops.insightservice.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightController {

    private final InsightService insightService;

    @GetMapping("/test")
    public String test() {
        return "Insight Service Working";
    }

    @GetMapping
    public List<Insight> getAllInsights(
            @RequestHeader("X-User-Id") String userId) {
        return insightService.getAllInsights(userId);
    }

    @GetMapping("/count")
    public long getInsightCount(
            @RequestHeader("X-User-Id") String userId) {
        return insightService.getInsightCount(userId);
    }

    @GetMapping("/{id}")
    public Insight getInsightById(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        return insightService.getInsightById(id, userId);
    }

    @DeleteMapping("/{id}")
    public String deleteInsight(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {

        insightService.deleteInsight(id, userId);
        return "Insight deleted successfully";
    }

    @DeleteMapping
    public String deleteAllInsights(
            @RequestHeader("X-User-Id") String userId) {

        insightService.deleteAllInsights(userId);
        return "All insights deleted successfully";
    }
    @DeleteMapping("/service/{serviceName}")
    public String deleteInsightsByService(
            @PathVariable String serviceName,
            @RequestHeader("X-User-Id") String userId) {

        insightService.deleteInsightsByService(serviceName, userId);

        return "Service insights deleted successfully";
    }
}
