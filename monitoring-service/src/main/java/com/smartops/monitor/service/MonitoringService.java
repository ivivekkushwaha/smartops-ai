package com.smartops.monitor.service;

import com.smartops.monitor.dto.*;
import com.smartops.monitor.model.ServiceStatus;

import java.util.List;
import java.util.Map;



public interface MonitoringService {

    // ==========================================
    // ADD SERVICE (NEW)
    // ==========================================

    ServiceStatus addService(ServiceStatusRequest request, String userId);

    // ==========================================
    // USER SERVICES
    // ==========================================

    List<ServiceStatus> getServicesByUser(String userId);

    ServiceStatus getServiceById(String serviceId, String userId);

    // ==========================================
    // SERVICE HEALTH
    // ==========================================

    ServiceStatus getServiceHealth(String serviceId, String userId);




    // ==========================================
    // SERVICE UPTIME
    // ==========================================


    // ==========================================
    // DASHBOARD (USER BASED)
    // ==========================================

    DashboardMetricsResponse getDashboardMetrics(String userId);



    // ==========================================
    // MONITORING CORE (SCHEDULER)
    // ==========================================

    void monitorServices();

    ServiceStatus updateService(String id, ServiceStatusRequest request, String userId);

    void deleteService(String id, String userId);

    void deleteServicesByUserId(String userId);
}
