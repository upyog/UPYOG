package org.upyog.dashboard.adv.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ADVAggregatedData {
    private Integer previousYearRevenue;
    private Integer currentFYCollection;
    private Integer totalApplicationsReceived;
    private Integer totalApplicationsRejected;
    private Integer totalApplicationApproved;
    
    private String transactionsJson;
}
