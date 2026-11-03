package com.smartops.monitor.service;

import com.smartops.monitor.kafka.KafkaProducerService;
import com.smartops.monitor.model.ServiceStatus;
import com.smartops.monitor.repository.ServiceStatusRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TenantIsolationTest {

    @Test
    void logQueriesAlwaysUseTheAuthenticatedUserId() {
        LogRepository repository = mock(LogRepository.class);
        LogService service = new LogService(repository);

        service.getRecentLogs("tenant-a", 50);

        verify(repository).findByUserIdOrderByTimestampDesc("tenant-a");
    }

    @Test
    void serviceLookupRejectsAnotherTenantsServiceId() {
        ServiceStatusRepository repository = mock(ServiceStatusRepository.class);
        ServiceStatus service = new ServiceStatus();
        service.setId("service-a");
        service.setUserId("tenant-a");
        when(repository.findById("service-a")).thenReturn(Optional.of(service));

        MonitoringServiceImpl monitoring = new MonitoringServiceImpl(
                repository,
                mock(KafkaProducerService.class)
        );

        assertThrows(RuntimeException.class,
                () -> monitoring.getServiceById("service-a", "tenant-b"));
    }
}
