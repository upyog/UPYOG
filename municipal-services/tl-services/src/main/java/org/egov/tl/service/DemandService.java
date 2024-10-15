package org.egov.tl.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.repository.DemandRepository;
import org.egov.tl.util.TLConstants;
import org.egov.tl.web.models.ApplicationDetail;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.contract.Demand;
import org.egov.tl.web.models.contract.DemandDetail;
import org.egov.tl.web.models.contract.DemandResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private TradeLicenseService tradeLicenseService;


    public List<Demand> generateDemand(RequestInfo requestInfo,TradeLicense license, String businessService){

    	log.info("#### generateDemand license: "+license);
    	// get total Tax
    	ApplicationDetail applicationDetail = tradeLicenseService.getApplicationBillUserDetail(license, requestInfo);
		
    	DemandDetail demandDetail = DemandDetail.builder()
    								.taxHeadMasterCode(TLConstants.BILLING_TAX_HEAD_MASTER_CODE)
    								.taxAmount(applicationDetail.getTotalPayableAmount())
    								.collectionAmount(BigDecimal.ZERO)
    								.build();
    	
    	Demand demandOne = Demand.builder()
                .consumerCode(license.getApplicationNumber())
                .demandDetails(Arrays.asList(demandDetail))
                .minimumAmountPayable(applicationDetail.getTotalPayableAmount())
                .tenantId(TLConstants.STATE_LEVEL_TENANT_ID)
                .taxPeriodFrom(new Date().getTime())
                .taxPeriodTo(license.getValidTo())
//                .taxPeriodTo(new Date((Calendar.getInstance().getTimeInMillis() + (long) 365 * 24 * 60 * 60 * 1000)).getTime())
                .consumerType(license.getBusinessService())
                .businessService(license.getBusinessService())
                .build();
    	
    	List<Demand> demands = Arrays.asList(demandOne);

    	log.info("#### demandRepository.saveDemand(requestInfo,demands) : "+demands);
    	log.info("Demand Details: Consumer Code: {}, Demand Details: {}, Minimum Amount Payable: {}, Tenant ID: {}, Tax Period From: {}, Tax Period To: {}, Consumer Type: {}, Business Service: {}", 
    	         demandOne.getConsumerCode(), demandOne.getDemandDetails(), demandOne.getMinimumAmountPayable(), demandOne.getTenantId(), demandOne.getTaxPeriodFrom(), demandOne.getTaxPeriodTo(), demandOne.getConsumerType(), demandOne.getBusinessService());
    	List<Demand> savedDemands =demandRepository.saveDemand(requestInfo,demands);
        log.info("#### savedDemands: "+savedDemands);
    	
    	if(CollectionUtils.isEmpty(savedDemands)) {
    		throw new CustomException("SAVE_DEMAND_FAILED","Failed to save Demand.");
    	}
    	
    	return savedDemands;
    }


    List<Demand> searchDemand(String tenantId,Set<String> consumerCodes,RequestInfo requestInfo, String businessService){
    	
    	RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
    	DemandResponse response = demandRepository.search(tenantId, consumerCodes, requestInfoWrapper, businessService);

        if(CollectionUtils.isEmpty(response.getDemands()))
            return null;

        else 
        	return response.getDemands();

    }


}
