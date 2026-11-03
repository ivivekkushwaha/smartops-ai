package com.smartops.monitor.controller;

import com.smartops.monitor.dto.*;
import com.smartops.monitor.model.ServiceStatus;
import com.smartops.monitor.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Validated
public class MonitoringController {

    private final MonitoringService monitoringService;

    // ==========================================
    // ADD SERVICE (NEW - CORE FEATURE)
    // ==========================================

    @PostMapping("/services")
    public ServiceStatus addService(
            @Valid @RequestBody ServiceStatusRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {
        return monitoringService.addService(request, userId);
    }

    // ==========================================
    // GET USER SERVICES
    // ==========================================

    @GetMapping("/services")
    public List<ServiceStatus> getUserServices(
            @RequestHeader("X-User-Id") String userId
    ) {
        return monitoringService.getServicesByUser(userId);
    }

    // ==========================================
    // GET SINGLE SERVICE
    // ==========================================

    @GetMapping("/services/{serviceId}")
    public ServiceStatus getServiceById(
            @PathVariable String serviceId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return monitoringService.getServiceById(serviceId, userId);
    }

    // 🔥 UPDATE SERVICE
    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceStatus> updateService(
            @PathVariable String id,
            @Valid @RequestBody ServiceStatusRequest request,
            @RequestHeader("X-User-Id") String userId
    ) {

        ServiceStatus updated = monitoringService.updateService(id, request, userId);
        return ResponseEntity.ok(updated);
    }

    // 🔥 DELETE SERVICE
    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteService(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId
    ) {

        monitoringService.deleteService(id, userId);
        return ResponseEntity.ok("Service deleted successfully");
    }

    // ==========================================
    // SERVICE HEALTH
    // ==========================================

    @GetMapping("/services/{serviceId}/health")
    public ServiceStatus getServiceHealth(
            @PathVariable String serviceId,
            @RequestHeader("X-User-Id") String userId
    ) {
        return monitoringService.getServiceHealth(serviceId, userId);
    }




    // ==========================================
    // SERVICE LOGS
    // ==========================================


    // ==========================================
    // SERVICE UPTIME
    // ==========================================


    // ==========================================
    // USER DASHBOARD (FILTERED)
    // ==========================================

    @GetMapping("/dashboard")
    public DashboardMetricsResponse getUserDashboard(
            @RequestHeader("X-User-Id") String userId
    ) {
        System.out.println("working");
        return monitoringService.getDashboardMetrics(userId);
    }


}
