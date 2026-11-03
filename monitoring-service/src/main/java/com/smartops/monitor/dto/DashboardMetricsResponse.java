package com.smartops.monitor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardMetricsResponse {

    private long totalServices;
    private long upServices;
    private long downServices;
}
