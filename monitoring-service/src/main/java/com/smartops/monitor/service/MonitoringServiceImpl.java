
package com.smartops.monitor.service;

import com.smartops.common.event.AlertEvent;
import com.smartops.monitor.dto.DashboardMetricsResponse;
import com.smartops.monitor.dto.ServiceStatusRequest;
import com.smartops.monitor.kafka.KafkaProducerService;
import com.smartops.monitor.model.ServiceStatus;
import com.smartops.monitor.repository.ServiceStatusRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    private final ServiceStatusRepository repository;
    private final KafkaProducerService producer;

    private final RestTemplate restTemplate = new RestTemplate();

    // ADD SERVICE
    @Override
    public ServiceStatus addService(
            ServiceStatusRequest request,
            String userId
    ) {

        ServiceStatus service = new ServiceStatus();

        service.setUserId(userId);
        service.setServiceName(request.getServiceName());
        service.setBaseUrl(request.getBaseUrl());
        service.setStatus("UNKNOWN");
        service.setResponseTime(0L);

        return repository.save(service);
    }

    // GET ALL USER SERVICES
    @Override
    public List<ServiceStatus> getServicesByUser(String userId) {
        return repository.findByUserId(userId);
    }

    // GET SERVICE BY ID
    @Override
    public ServiceStatus getServiceById(
            String serviceId,
            String userId
    ) {

        ServiceStatus service = repository.findById(serviceId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Service not found"
                        )
                );

        if (!userId.equals(service.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Service not found"
            );
        }

        return service;
    }

    // GET SERVICE HEALTH
    @Override
    public ServiceStatus getServiceHealth(
            String serviceId,
            String userId
    ) {
        return getServiceById(serviceId, userId);
    }

    // DASHBOARD STATISTICS
    @Override
    public DashboardMetricsResponse getDashboardMetrics(String userId) {

        List<ServiceStatus> services = repository.findByUserId(userId);

        long total = services.size();

        long up = services.stream()
                .filter(s -> "UP".equalsIgnoreCase(s.getStatus()))
                .count();

        long down = services.stream()
                .filter(s -> "DOWN".equalsIgnoreCase(s.getStatus()))
                .count();

        return DashboardMetricsResponse.builder()
                .totalServices(total)
                .upServices(up)
                .downServices(down)
                .build();
    }

    // AUTOMATIC SERVICE MONITORING
    @Override
    @Scheduled(fixedRateString = "${monitoring.polling-rate}")
    public void monitorServices() {

        List<ServiceStatus> services = repository.findAll();

        for (ServiceStatus service : services) {

            String previousStatus = service.getStatus();

            long startTime = System.currentTimeMillis();

            try {

                String healthUrl = service.getBaseUrl() + "/actuator/health";

                HealthResponse response = restTemplate.getForObject(
                        healthUrl,
                        HealthResponse.class
                );

                if (response == null ||
                        !"UP".equalsIgnoreCase(response.getStatus())) {

                    throw new IllegalStateException("Service health is not UP");
                }

                long responseTime =
                        System.currentTimeMillis() - startTime;

                service.setStatus("UP");
                service.setResponseTime(responseTime);

            } catch (Exception e) {

                service.setStatus("DOWN");
                service.setResponseTime(-1L);

                // Generate alert only when status changes to DOWN
                if (!"DOWN".equalsIgnoreCase(previousStatus)) {

                    AlertEvent event = new AlertEvent(
                            service.getUserId(),
                            service.getServiceName(),
                            "CRITICAL",
                            "Service health check failed",
                            "DOWN",
                            LocalDateTime.now(),
                            "Service Down"
                    );

                    producer.sendAlert(event);
                }
            }

            service.setLastChecked(LocalDateTime.now());

            repository.save(service);
        }
    }

    // UPDATE SERVICE
    @Override
    public ServiceStatus updateService(
            String id,
            ServiceStatusRequest request,
            String userId
    ) {

        ServiceStatus service = getServiceById(id, userId);

        service.setServiceName(request.getServiceName());
        service.setBaseUrl(request.getBaseUrl());

        service.setStatus("UNKNOWN");
        service.setResponseTime(0L);

        return repository.save(service);
    }

    // DELETE SERVICE
    @Override
    public void deleteService(String id, String userId) {

        ServiceStatus service = getServiceById(id, userId);

        repository.delete(service);
    }

    // DELETE ALL SERVICES OF A USER
    @Override
    public void deleteServicesByUserId(String userId) {
        repository.deleteByUserId(userId);
    }

    // ACTUATOR HEALTH RESPONSE
    private static class HealthResponse {

        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
