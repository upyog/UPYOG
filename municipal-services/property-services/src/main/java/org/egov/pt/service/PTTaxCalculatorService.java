package org.egov.pt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.egov.pt.repository.PTTaxCalculatorRepository;

@Service
public class PTTaxCalculatorService {

    @Autowired
    private PTTaxCalculatorRepository ptTaxCalculatorRepository;

    public void cancelTrackerRecord(String tenantId, String consumerCode, String userId) {
    	ptTaxCalculatorRepository.updateTrackerStatus(tenantId, consumerCode, "CANCELLED", userId);
    }
}
