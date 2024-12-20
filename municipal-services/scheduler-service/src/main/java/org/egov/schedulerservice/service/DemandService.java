//package org.egov.schedulerservice.service;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//
//import org.egov.common.contract.request.RequestInfo;
//import org.egov.schedulerservice.contract.bill.Demand;
//import org.egov.schedulerservice.contract.bill.DemandDetail;
//import org.egov.schedulerservice.contract.bill.DemandRepository;
//import org.egov.schedulerservice.contract.bill.DemandResponse;
//import org.egov.schedulerservice.util.RequestInfoWrapper;
//import org.egov.schedulerservice.util.SchedulerConstants;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//@Service
//public class DemandService {
//
////    @Autowired
////    private CalculationService calculationService;
//
//    @Autowired
//    private DemandRepository demandRepository;
//
//	public List<Demand> generateDemand(RequestInfo requestInfo,/*GarbageAccount garbageAccount,*/ String businessService){
//
//    	// get total Tax
////    	ApplicationDetail applicationDetail = tradeLicenseService.getApplicationBillUserDetail(garbageAccount, requestInfo);
//    	BigDecimal taxAmount = new BigDecimal("100.00");
//    	
//    	DemandDetail demandDetail = DemandDetail.builder()
//    								.taxHeadMasterCode(SchedulerConstants.BILLING_TAX_HEAD_MASTER_CODE)
//    								.taxAmount(taxAmount)
//    								.collectionAmount(BigDecimal.ZERO)
//    								.build();
//    	// create demand of 1 month
//    	Demand demandOne = Demand.builder()
//                .consumerCode(null)//(garbageAccount.getGrbgApplicationNumber())
//                .demandDetails(Arrays.asList(demandDetail))
//                .minimumAmountPayable(taxAmount)
//                .tenantId(null)//(garbageAccount.getTenantId())
//                .taxPeriodFrom(new Date().getTime())
//                .taxPeriodTo(new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000)).getTime())
////                .taxPeriodTo(new Date((Calendar.getInstance().getTimeInMillis() + (long) 365 * 24 * 60 * 60 * 1000)).getTime())
//                .consumerType(null)//(garbageAccount.getGrbgApplicationNumber())
//                .businessService(null)//(GrbgConstants.BUSINESS_SERVICE)
//                .build();
//    	
//    	List<Demand> demands = Arrays.asList(demandOne);
//    	
//    	List<Demand> savedDemands = demandRepository.saveDemand(requestInfo,demands);
//    	
//    	return savedDemands;
//    }
//
//
//    List<Demand> searchDemand(String tenantId,Set<String> consumerCodes,RequestInfo requestInfo, String businessService){
//    	
//    	RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
//    	DemandResponse response = demandRepository.search(tenantId, consumerCodes, requestInfoWrapper, businessService);
//
//        if(CollectionUtils.isEmpty(response.getDemands()))
//            return null;
//
//        else 
//        	return response.getDemands();
//
//    }
//
//
//}
