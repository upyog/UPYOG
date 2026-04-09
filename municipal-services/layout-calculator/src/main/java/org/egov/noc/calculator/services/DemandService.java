package org.egov.noc.calculator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.noc.calculator.web.models.RequestInfoWrapper;
import org.egov.noc.calculator.web.models.demand.Demand;
import org.egov.noc.calculator.web.models.demand.DemandDetail;
import org.egov.noc.calculator.web.models.demand.DemandRequest;
import org.egov.noc.calculator.web.models.demand.DemandResponse;
import org.egov.noc.calculator.web.models.demand.TaxHeadEstimate;
import org.egov.tracer.model.CustomException;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.noc.calculator.config.LAYOUTCalculatorConfig;
import org.egov.noc.calculator.repository.DemandRepository;
import org.egov.noc.calculator.repository.ServiceRequestRepository;
import org.egov.noc.calculator.utils.CalculatorUtils;
import org.egov.noc.calculator.utils.LAYOUTConstants;
import org.egov.noc.calculator.web.models.Calculation;
import org.egov.noc.calculator.web.models.Layout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.noc.calculator.web.models.bill.GetBillCriteria;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DemandService {

    @Autowired
    private LAYOUTCalculatorConfig nocConfiguration;

    @Autowired
    private CalculatorUtils utils;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ServiceRequestRepository repository;
    
    @Autowired
    private DemandRepository demandRepository;

    public List<Demand> generateDemands(RequestInfo requestInfo, List<Calculation> calculations,String feeType) {
		
		//List that will contain Calculation for new demands
        List<Calculation> createCalculations = new LinkedList<>();

        //List that will contain Calculation for old demands
        List<Calculation> updateCalculations = new LinkedList<>();
		
		String tenantId = calculations.get(0).getTenantId();
		Set<String> applicationNos = calculations.stream().map(calculation -> calculation.getLayout().getApplicationNo())
				.collect(Collectors.toSet());
		List<Demand> searchedDemands = searchDemand(tenantId, applicationNos, requestInfo, calculations.get(0), LAYOUTConstants.LAYOUT_BUSINESS_SERVICE + "." + feeType, "");
		List<Demand> demands = searchedDemands.stream().filter(demand -> !demand.getIsPaymentCompleted()).collect(Collectors.toList());

		Set<String> applicationNumbersFromDemands = new HashSet<>();
        if(!CollectionUtils.isEmpty(demands))
            applicationNumbersFromDemands = demands.stream().map(Demand::getConsumerCode).collect(Collectors.toSet());
		
      //If demand already exists add it updateCalculations else createCalculations
        for(Calculation calculation : calculations)
        {      if(!applicationNumbersFromDemands.contains(calculation.getLayout().getApplicationNo()))
                    createCalculations.add(calculation);
                else
                    updateCalculations.add(calculation);
        }
        
        demands = new ArrayList<>();
        
        if(!CollectionUtils.isEmpty(createCalculations))
        	demands = createDemand(requestInfo,createCalculations, feeType, searchedDemands);

        if(!CollectionUtils.isEmpty(updateCalculations))
        	demands = updateDemand(requestInfo,updateCalculations, feeType, searchedDemands);
        
		return demands;
	}



    /**
     * Updates demand for the given list of calculations
     * @param requestInfo The RequestInfo of the calculation request
     * @param calculations List of calculation object
     * @return Demands that are updated
     */
    private List<Demand> updateDemand(RequestInfo requestInfo,List<Calculation> calculations, String feeType, List<Demand> searchedDemands){
        List<Demand> demands = new LinkedList<>();
        for(Calculation calculation : calculations) {
        	
            if(CollectionUtils.isEmpty(searchedDemands))
                throw new CustomException(LAYOUTConstants.INVALID_UPDATE,"No demand exists for applicationNumber: "+calculation.getLayout().getApplicationNo());

            Demand demand = searchedDemands.stream().filter(d -> !d.getIsPaymentCompleted())
            		.findFirst().orElse(searchedDemands.get(searchedDemands.size() -1 ));
            List<DemandDetail> demandDetails = searchedDemands.stream().flatMap(d -> d.getDemandDetails().stream()).collect(Collectors.toList());
            List<DemandDetail> updatedDemandDetails = getUpdatedDemandDetails(calculation,demandDetails);
            updatedDemandDetails = updatedDemandDetails.stream()
            		.filter(demandDetail -> demandDetail.getDemandId() == null || demandDetail.getDemandId().equalsIgnoreCase(demand.getId()))
            		.collect(Collectors.toList());
            demand.setDemandDetails(updatedDemandDetails);
            demands.add(demand);
        }
         return demandRepository.updateDemand(requestInfo,demands);
    }

    /**
     * Returns the list of new DemandDetail to be added for updating the demand
     * @param calculation The calculation object for the update tequest
     * @param demandDetails The list of demandDetails from the existing demand
     * @return The list of new DemandDetails
     */
    private List<DemandDetail> getUpdatedDemandDetails(Calculation calculation, List<DemandDetail> demandDetails){

        List<DemandDetail> newDemandDetails = new ArrayList<>();
        Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();

        demandDetails.forEach(demandDetail -> {
            if(!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())){
                List<DemandDetail> demandDetailList = new LinkedList<>();
                demandDetailList.add(demandDetail);
                taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(),demandDetailList);
            }
            else
              taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
        });

        BigDecimal diffInTaxAmount;
        List<DemandDetail> demandDetailList;
        BigDecimal total;

        for(TaxHeadEstimate taxHeadEstimate : calculation.getTaxHeadEstimates()){
            if(!taxHeadToDemandDetail.containsKey(taxHeadEstimate.getTaxHeadCode()))
                newDemandDetails.add(
                        DemandDetail.builder()
                                .taxAmount(taxHeadEstimate.getEstimateAmount())
                                .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
                                .tenantId(calculation.getTenantId())
                                .collectionAmount(BigDecimal.ZERO)
                                .build());
            else {
                 demandDetailList = taxHeadToDemandDetail.get(taxHeadEstimate.getTaxHeadCode());
                 total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                 diffInTaxAmount = taxHeadEstimate.getEstimateAmount().subtract(total);
                 if(diffInTaxAmount.compareTo(BigDecimal.ZERO)!=0) {
                     newDemandDetails.add(
                             DemandDetail.builder()
                                     .taxAmount(diffInTaxAmount)
                                     .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
                                     .tenantId(calculation.getTenantId())
                                     .collectionAmount(BigDecimal.ZERO)
                                     .build());
                 }
            }
        }
        List<DemandDetail> combinedBillDetials = new LinkedList<>(demandDetails);
        combinedBillDetials.addAll(newDemandDetails);
        addRoundOffTaxHead(calculation.getTenantId(),combinedBillDetials);
        return combinedBillDetials;
    }

    /**
     * Searches demand for the given consumerCode and tenantIDd
     * @param tenantId The tenantId of the tradeLicense
     * @param consumerCodes The set of consumerCode of the demands
     * @param requestInfo The RequestInfo of the incoming request
     * @return Lis to demands for the given consumerCode
     */
    private List<Demand> searchDemand(String tenantId,Set<String> consumerCodes,RequestInfo requestInfo,Calculation calculation,String businessservice, String isPaymentCompleted){
    	String uri = utils.getDemandSearchURL(isPaymentCompleted);
        uri = uri.replace("{1}",tenantId);
        uri = uri.replace("{2}", businessservice);
        uri = uri.replace("{3}",StringUtils.join(consumerCodes, ','));
        if(!StringUtils.isEmpty(isPaymentCompleted))
    		uri = uri.replace("{4}",isPaymentCompleted);

        Object result = repository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
                                                      .requestInfo(requestInfo).build());

        DemandResponse response;
        try {
             response = mapper.convertValue(result,DemandResponse.class);
        }
        catch (IllegalArgumentException e){
            throw new CustomException(LAYOUTConstants.PARSING_ERROR,"Failed to parse response from Demand Search");
        }

        if(CollectionUtils.isEmpty(response.getDemands()))
            return new ArrayList<>();

        else return response.getDemands();

    }
    
    
    /**
     * Creates demand for the given list of calculations
     * @param requestInfo The RequestInfo of the calculation request
     * @param calculations List of calculation object
     * @return Demands that are created
     */
    private List<Demand> createDemand(RequestInfo requestInfo,List<Calculation> calculations,String feeType, List<Demand> searchedDemands){
        List<Demand> demands = new LinkedList<>();
        for(Calculation calculation : calculations) {
        	Layout layout = null;

            if(calculation.getLayout()!=null) {
            	layout = calculation.getLayout();
            }


            Object additionalDetailsData = layout.getLayoutDetails().getAdditionalDetails();

            // Cast to LinkedHashMap
            LinkedHashMap<String, Object> additionalDetailsMap = (LinkedHashMap<String, Object>) additionalDetailsData;
            Map<String, Object> siteDetails = (Map<String, Object>) additionalDetailsMap.get("siteDetails");
            String businessservice = (String) siteDetails.get("businessService");



            String tenantId = calculation.getTenantId();
            String consumerCode = calculation.getLayout().getApplicationNo();

            User owner = layout.getOwners().get(0).toCommonUser();
            
           	List<DemandDetail> demandDetails = new LinkedList<>();

            calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
                demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
                        .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
                        .collectionAmount(BigDecimal.ZERO)
                        .tenantId(calculation.getTenantId())
                        .build());
            });
            
            List<Demand> searchResult = searchedDemands.stream().filter(demand -> demand.getIsPaymentCompleted()).collect(Collectors.toList());
            
            if(!CollectionUtils.isEmpty(searchResult)) {
            	Demand latestDemand = searchResult.stream().max(Comparator.comparingLong(Demand::getTaxPeriodTo)).get();
            	latestDemand.setTaxPeriodTo(System.currentTimeMillis());
            	demandRepository.updateDemand(requestInfo, Collections.singletonList(latestDemand));
            	
            	List<DemandDetail> allPaidDemandDetails = searchResult.stream()
            			.map(Demand::getDemandDetails)
            			.flatMap(List::stream).collect(Collectors.toList());            	
            	
            	List<DemandDetail> updatedDemandDetails = getRemainingDemandDetails(demandDetails, allPaidDemandDetails, calculation.getTenantId());
            	demandDetails.clear();
            	demandDetails.addAll(updatedDemandDetails);
            }

            Demand demand = Demand.builder()
                    .tenantId(calculation.getTenantId())
                    .consumerCode(calculation.getApplicationNumber())
                    .consumerType("LAYOUT-" + LAYOUTConstants.MDMS_CHARGES_TYPE_CODE)
                    .businessService(LAYOUTConstants.LAYOUT_BUSINESS_SERVICE + "." + feeType)
                    .payer(owner)
                    .minimumAmountPayable(nocConfiguration.getMinimumPayableAmount())
                    .taxPeriodFrom(calculation.getTaxPeriodFrom())
                    .taxPeriodTo(calculation.getTaxPeriodTo())
                    .demandDetails(demandDetails)
                    .isPaymentCompleted(false)
                    .build();

            demands.add(demand);

        }
        return demandRepository.saveDemand(requestInfo,demands);
    }
    
    public DemandResponse updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {

        if (getBillCriteria.getAmountExpected() == null) getBillCriteria.setAmountExpected(BigDecimal.ZERO);
//        validator.validateGetBillCriteria(getBillCriteria);
        RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
//        Map<String, Map<String, List<Object>>> propertyBasedExemptionMasterMap = new HashMap<>();
//        Map<String, JSONArray> timeBasedExmeptionMasterMap = new HashMap<>();
//        mstrDataService.setPropertyMasterValues(requestInfo, getBillCriteria.getTenantId(),
//                propertyBasedExemptionMasterMap, timeBasedExmeptionMasterMap);

/*
		if(CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes()))
			getBillCriteria.setConsumerCodes(Collections.singletonList(getBillCriteria.getPropertyId()+ CalculatorConstants.PT_CONSUMER_CODE_SEPARATOR +getBillCriteria.getAssessmentNumber()));
*/

        DemandResponse res = mapper.convertValue(
                repository.fetchResult(utils.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
                DemandResponse.class);
        if (CollectionUtils.isEmpty(res.getDemands())) {
            Map<String, String> map = new HashMap<>();
            map.put(LAYOUTConstants.EMPTY_DEMAND_ERROR_CODE, LAYOUTConstants.EMPTY_DEMAND_ERROR_MESSAGE);
            	throw new CustomException(map);
        }


        /**
         * Loop through the consumerCodes and re-calculate the time based applicables
         */


        Map<String,List<Demand>> consumerCodeToDemandMap = new HashMap<>();
        res.getDemands().forEach(demand -> {
            if(consumerCodeToDemandMap.containsKey(demand.getConsumerCode()))
                consumerCodeToDemandMap.get(demand.getConsumerCode()).add(demand);
            else {
                List<Demand> demands = new LinkedList<>();
                demands.add(demand);
                consumerCodeToDemandMap.put(demand.getConsumerCode(),demands);
            }
        });

        if (!CollectionUtils.isEmpty(consumerCodeToDemandMap)) {
            List<Demand> demandsToBeUpdated = new LinkedList<>();

            String tenantId = getBillCriteria.getTenantId();

            /**
             * Call demand update in bulk to update the interest or penalty
             */
            DemandRequest request = DemandRequest.builder().demands(demandsToBeUpdated).requestInfo(requestInfo).build();
            StringBuilder updateDemandUrl = utils.getUpdateDemandUrl();
//            repository.fetchResult(updateDemandUrl, request);
        }
        return res;
    }

    /**
     * Adds roundOff taxHead if decimal values exists
     * @param tenantId The tenantId of the demand
     * @param demandDetails The list of demandDetail
     */
    private void addRoundOffTaxHead(String tenantId,List<DemandDetail> demandDetails){
        BigDecimal totalTax = BigDecimal.ZERO;

        DemandDetail prevRoundOffDemandDetail = null;

        /*
        * Sum all taxHeads except RoundOff as new roundOff will be calculated
        * */
        for (DemandDetail demandDetail : demandDetails){
        	/*if(!demandDetail.getTaxHeadMasterCode().equalsIgnoreCase(BPACalculatorConstants.MDMS_ROUNDOFF_TAXHEAD))
                totalTax = totalTax.add(demandDetail.getTaxAmount());
            else*/
             prevRoundOffDemandDetail = demandDetail;
        }

        BigDecimal decimalValue = totalTax.remainder(BigDecimal.ONE);
        BigDecimal midVal = new BigDecimal(0.5);
        BigDecimal roundOff = BigDecimal.ZERO;

        /*
        * If the decimal amount is greater than 0.5 we subtract it from 1 and put it as roundOff taxHead
        * so as to nullify the decimal eg: If the tax is 12.64 we will add extra tax roundOff taxHead
        * of 0.36 so that the total becomes 13
        * */
        if(decimalValue.compareTo(midVal) > 0)
            roundOff = BigDecimal.ONE.subtract(decimalValue);


        /*
         * If the decimal amount is less than 0.5 we put negative of it as roundOff taxHead
         * so as to nullify the decimal eg: If the tax is 12.36 we will add extra tax roundOff taxHead
         * of -0.36 so that the total becomes 12
         * */
        if(decimalValue.compareTo(midVal) < 0)
            roundOff = decimalValue.negate();

        /*
        * If roundOff already exists in previous demand create a new roundOff taxHead with roundOff amount
        * equal to difference between them so that it will be balanced when bill is generated. eg: If the
        * previous roundOff amount was of -0.36 and the new roundOff excluding the previous roundOff is
        * 0.2 then the new roundOff will be created with 0.2 so that the net roundOff will be 0.2 -(-0.36)
        * */
     /*   if(prevRoundOffDemandDetail!=null){
            roundOff = roundOff.subtract(prevRoundOffDemandDetail.getTaxAmount());
        }*/

        if(roundOff.compareTo(BigDecimal.ZERO)!=0){
                 DemandDetail roundOffDemandDetail = DemandDetail.builder()
                    .taxAmount(roundOff)
                    .taxHeadMasterCode(LAYOUTConstants.MDMS_ROUNDOFF_TAXHEAD)
                    .tenantId(tenantId)
                    .collectionAmount(BigDecimal.ZERO)
                    .build();

            demandDetails.add(roundOffDemandDetail);
        }
    }
    
    /**
     * Returns the list of new DemandDetail to be added for creation the demand for remaining amount
     * @param demandDetails The list of demandDetails to be updated
     * @param searchDemandDetails The list of demandDetails from the existing demand
     * @return The list of new DemandDetails
     */
    private List<DemandDetail> getRemainingDemandDetails(List<DemandDetail> demandDetails, List<DemandDetail> searchDemandDetails, String tenantId){

        List<DemandDetail> newDemandDetails = new ArrayList<>();
        Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();

        searchDemandDetails.forEach(demandDetail -> {
            if(!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())){
                List<DemandDetail> demandDetailList = new LinkedList<>();
                demandDetailList.add(demandDetail);
                taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(),demandDetailList);
            }
            else
              taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
        });

        BigDecimal diffInTaxAmount;
        List<DemandDetail> demandDetailList;
        BigDecimal total;
        
        for(DemandDetail demandDetail : demandDetails){
            if(!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode()))
                newDemandDetails.add(demandDetail);
            else {
                 demandDetailList = taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode());
                 total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                 diffInTaxAmount = demandDetail.getTaxAmount().subtract(total);
                 if(diffInTaxAmount.compareTo(BigDecimal.ZERO)!=-1) {
                	 demandDetail.setTaxAmount(diffInTaxAmount);
                 }else {
                	 demandDetail.setTaxAmount(BigDecimal.ZERO);
                 }
                 newDemandDetails.add(demandDetail);

            }
        }
        addRoundOffTaxHead(tenantId,newDemandDetails);
        return newDemandDetails;
    }
    
}
