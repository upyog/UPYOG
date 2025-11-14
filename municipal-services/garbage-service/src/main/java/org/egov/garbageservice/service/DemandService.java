package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.egov.common.contract.request.User;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.UserSearchResponse;
import org.egov.garbageservice.service.UserService;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.Demand.StatusEnum;
import org.egov.garbageservice.contract.bill.DemandDetail;
import org.egov.garbageservice.contract.bill.DemandRepository;
import org.egov.garbageservice.contract.bill.DemandResponse;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GenerateBillRequest;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class DemandService {

	@Autowired
	private GrbgConstants grbgConfig;

	@Autowired
	private DemandRepository demandRepository;
	
	@Autowired
	private UserService userService;

	public List<Demand> generateDemand(RequestInfo requestInfo, GarbageAccount garbageAccount, String businessService,
			BigDecimal taxAmount, GenerateBillRequest generateBillRequest) {

		Long taxPeriodFrom = getDateToTimeStamp(generateBillRequest.getFromDate(),generateBillRequest.getType(),"FROM");
//				null != generateBillRequest.getFromDate() ? generateBillRequest.getFromDate().getTime()
//				: new Date(Calendar.getInstance().getTimeInMillis()).getTime();
		Long taxPeriodTo = getDateToTimeStamp(generateBillRequest.getToDate(),generateBillRequest.getType(),"TO");
//				null != generateBillRequest.getToDate() ? generateBillRequest.getToDate().getTime()
//				: new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000)).getTime();
		DemandDetail demandDetail = DemandDetail.builder().taxHeadMasterCode(GrbgConstants.BILLING_TAX_HEAD_MASTER_CODE)
				.taxAmount(taxAmount).collectionAmount(BigDecimal.ZERO).build();
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.DAY_OF_MONTH, Integer.valueOf(grbgConfig.getGrbgBillExpiryAfter()));
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		Map<String, Object> additionalDetail = (Map<String, Object>) generateBillRequest.getAdditionalDetail();

		String consumercode = null;
		if(!businessService.equals("GB"))
			consumercode = getConsumerCodeBulk(garbageAccount.getGrbgApplicationNumber(),additionalDetail.get("bulkType").toString());
		else
			consumercode = garbageAccount.getGrbgApplicationNumber();
			
		Demand demandOne = Demand.builder().consumerCode(consumercode)
				.demandDetails(Arrays.asList(demandDetail)).minimumAmountPayable(taxAmount)
				.tenantId(garbageAccount.getTenantId()).taxPeriodFrom(taxPeriodFrom).taxPeriodTo(taxPeriodTo)
				.fixedBillExpiryDate(cal.getTimeInMillis()).consumerType(GrbgConstants.WORKFLOW_MODULE_NAME)
				.payer(User.builder().uuid(garbageAccount.getUserUuid()).build())
				.additionalDetails(generateBillRequest.getAdditionalDetail())
				.businessService(businessService).build();

		List<Demand> demands = Arrays.asList(demandOne);

		List<Demand> savedDemands = demandRepository.saveDemand(requestInfo, demands);

		return savedDemands;
	}
	
	private String getConsumerCodeBulk(String consumerCode, String bulkType) {
	    // generate random 2-digit number (10–99)
	    int randomNum = new Random().nextInt(90) + 10;  
	    return consumerCode + "_" + bulkType + "_" + randomNum;
	}
	private Long getDateToTimeStamp(Date date,String type,String FromTo) {
	
			if("ON-DEMAND".equals(type)) {
				if(date !=null) {
				 	Calendar now = Calendar.getInstance();
			        int hour = now.get(Calendar.HOUR_OF_DAY);
			        int minute = now.get(Calendar.MINUTE);
			        int second = now.get(Calendar.SECOND);
			        int millis = now.get(Calendar.MILLISECOND);
			
			        // Combine original date with current time
			        Calendar cal = Calendar.getInstance();
			        cal.setTime(date);
			        cal.set(Calendar.HOUR_OF_DAY, hour);
			        cal.set(Calendar.MINUTE, minute);
			        cal.set(Calendar.SECOND, second);
			        cal.set(Calendar.MILLISECOND, millis);
			        return cal.getTimeInMillis();
				}else 
					return "FROM".equals(FromTo) ? new Date(Calendar.getInstance().getTimeInMillis()).getTime():new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000)).getTime();
			}else {
				if(date !=null) 
					return date.getTime();
				else {
					return "FROM".equals(FromTo) ? new Date(Calendar.getInstance().getTimeInMillis()).getTime():new Date((Calendar.getInstance().getTimeInMillis() + (long) 30 * 24 * 60 * 60 * 1000)).getTime();
				}
			}
		}
	

	List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, RequestInfo requestInfo,
			String businessService) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		DemandResponse response = demandRepository.search(tenantId, consumerCodes, requestInfoWrapper, businessService);

		if (CollectionUtils.isEmpty(response.getDemands()))
			return null;

		else
			return response.getDemands();

	}
	
	List<Demand> searchDemandId(String tenantId, Set<String> demandIds, RequestInfo requestInfo,
			String businessService) {

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		DemandResponse response = demandRepository.searchId(tenantId, demandIds, requestInfoWrapper, businessService);

		if (CollectionUtils.isEmpty(response.getDemands()))
			return null;

		else
			return response.getDemands();

	}
	
    public List<Demand> cancelDemand(String tenantId, Set<String> demandIds, RequestInfo requestInfo,
			String businessService){
        List<Demand> demands = new LinkedList<>();
//        for(Calculation calculation : calculations) {

            List<Demand> searchResult = searchDemandId(tenantId,demandIds, requestInfo,businessService);

            if(CollectionUtils.isEmpty(searchResult))
                throw new CustomException("INVALID UPDATE","No demand exists for applicationNumber: ");
            
            Demand demand = searchResult.get(0);
            	demand.setStatus(StatusEnum.CANCELLED);
//            List<DemandDetail> demandDetails = demand.getDemandDetails();
//            List<DemandDetail> updatedDemandDetails = getUpdatedDemandDetails(calculation,demandDetails);
//            demand.setDemandDetails(updatedDemandDetails);
            demands.add(demand);
//        }
         return demandRepository.updateDemand(requestInfo,demands);
    }

}
