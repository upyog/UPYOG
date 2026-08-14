package org.upyog.dashboard.adv.dto;

import lombok.Data;

@Data
public class ADVAggregatedData {
    private Integer previousYearRevenue;
    private Integer currentFYCollection;
    private Integer totalApplicationsReceived;
    private Integer totalApplicationsRejected;
    private Integer totalApplicationApproved;
    
    private String transactionsJson;
}
