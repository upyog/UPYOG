package org.egov.ndc.calculator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.ndc.calculator.config.NDCCalculatorConfig;
import org.egov.ndc.calculator.repository.ServiceRequestRepository;
import org.egov.ndc.calculator.utils.CalculatorUtils;
import org.egov.ndc.calculator.web.models.Calculation;
import org.egov.ndc.calculator.web.models.CalculationReq;
import org.egov.ndc.calculator.web.models.RequestInfoWrapper;
import org.egov.ndc.calculator.web.models.bill.GetBillCriteria;
import org.egov.ndc.calculator.web.models.demand.Demand;
import org.egov.ndc.calculator.web.models.demand.DemandDetail;
import org.egov.ndc.calculator.web.models.demand.DemandRequest;
import org.egov.ndc.calculator.web.models.demand.DemandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.egov.ndc.calculator.utils.NDCConstants.EMPTY_DEMAND_ERROR_CODE;
import static org.egov.ndc.calculator.utils.NDCConstants.EMPTY_DEMAND_ERROR_MESSAGE;

@Service
public class DemandService {

    @Autowired
    private NDCCalculatorConfig ndcConfiguration;

    @Autowired
    private CalculatorUtils utils;

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ServiceRequestRepository repository;

    public List<Demand> generateDemands(RequestInfo requestInfo, List<Calculation> calculations, CalculationReq calculationReq) {
        List<Demand> demands = new ArrayList<>();

        for (Calculation calculation : calculations) {
            DemandDetail demandDetail = DemandDetail.builder()
                    .tenantId(calculation.getTenantId())
                    .taxAmount(BigDecimal.valueOf(calculation.getTotalAmount()))
                    .taxHeadMasterCode(ndcConfiguration.getTaxHeadMasterCode()).build();

            User owner = calculationReq.getCalculationCriteria().get(0).getNdcApplicationRequest().getApplications().get(0).getOwners().get(0).toCommonUser();
            Demand demand = Demand.builder()
                    .tenantId(calculation.getTenantId()).consumerCode(calculation.getApplicationNumber())
                    .consumerType("NDC_APPLICATION_FEE")
                    .businessService("NDC")
                    .payer(owner)
                    .taxPeriodFrom(System.currentTimeMillis()).taxPeriodTo(System.currentTimeMillis())
                    .demandDetails(Collections.singletonList(demandDetail))
                    .build();

            demands.add(demand);
        }

        StringBuilder url = new StringBuilder().append(ndcConfiguration.getBillingHost())
                .append(ndcConfiguration.getDemandCreateEndpoint());

        DemandRequest demandRequest = DemandRequest.builder().requestInfo(requestInfo).demands(demands).build();

        Object response = repository.fetchResult(url, demandRequest);

        DemandResponse demandResponse = mapper.convertValue(response, DemandResponse.class);
        return demandResponse.getDemands();
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
            map.put(EMPTY_DEMAND_ERROR_CODE, EMPTY_DEMAND_ERROR_MESSAGE);
            //	throw new CustomException(map);
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

//            List<TaxPeriod> taxPeriods = mstrDataService.getTaxPeriodList(requestInfoWrapper.getRequestInfo(), tenantId);

//            for (String consumerCode : getBillCriteria.getConsumerCodes()) {
//                List<Demand> demands = consumerCodeToDemandMap.get(consumerCode);
//                if (CollectionUtils.isEmpty(demands))
//                    continue;

//                for(Demand demand : demands){
//                    if (demand.getStatus() != null
//                            && CalculatorConstants.DEMAND_CANCELLED_STATUS.equalsIgnoreCase(demand.getStatus().toString()))
//                        throw new CustomException(CalculatorConstants.EG_PT_INVALID_DEMAND_ERROR,
//                                CalculatorConstants.EG_PT_INVALID_DEMAND_ERROR_MSG);
//
//                    applytimeBasedApplicables(demand, requestInfoWrapper, timeBasedExmeptionMasterMap,taxPeriods);
//
//                    roundOffDecimalForDemand(demand, requestInfoWrapper);
//
//                    demandsToBeUpdated.add(demand);
//                }
//            }


            /**
             * Call demand update in bulk to update the interest or penalty
             */
            DemandRequest request = DemandRequest.builder().demands(demandsToBeUpdated).requestInfo(requestInfo).build();
            StringBuilder updateDemandUrl = utils.getUpdateDemandUrl();
//            repository.fetchResult(updateDemandUrl, request);
        }
        return res;
    }


}

//    public DemandResponse updateDemands(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {
//
//        if (getBillCriteria.getAmountExpected() == null) getBillCriteria.setAmountExpected(BigDecimal.ZERO);
////        validator.validateGetBillCriteria(getBillCriteria);
//        RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
//        Map<String, Map<String, List<Object>>> propertyBasedExemptionMasterMap = new HashMap<>();
//        Map<String, JSONArray> timeBasedExmeptionMasterMap = new HashMap<>();
////        mstrDataService.setPropertyMasterValues(requestInfo, getBillCriteria.getTenantId(),
////                propertyBasedExemptionMasterMap, timeBasedExmeptionMasterMap);
//
///*
//		if(CollectionUtils.isEmpty(getBillCriteria.getConsumerCodes()))
//			getBillCriteria.setConsumerCodes(Collections.singletonList(getBillCriteria.getPropertyId()+ CalculatorConstants.PT_CONSUMER_CODE_SEPARATOR +getBillCriteria.getAssessmentNumber()));
//*/
//        NDCCalculatorConfig config = new NDCCalculatorConfig();
//        DemandResponse res = mapper.convertValue(
//                repository.fetchResult(config.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
//                DemandResponse.class);
////        if (CollectionUtils.isEmpty(res.getDemands())) {
////            Map<String, String> map = new HashMap<>();
////            map.put(CalculatorConstants.EMPTY_DEMAND_ERROR_CODE, CalculatorConstants.EMPTY_DEMAND_ERROR_MESSAGE);
////            //	throw new CustomException(map);
////        }


        /**
         * Loop through the consumerCodes and re-calculate the time based applicables
         */
//
//
//        Map<String,List<Demand>> consumerCodeToDemandMap = new HashMap<>();
//        res.getDemands().forEach(demand -> {
//            if(consumerCodeToDemandMap.containsKey(demand.getConsumerCode()))
//                consumerCodeToDemandMap.get(demand.getConsumerCode()).add(demand);
//            else {
//                List<Demand> demands = new LinkedList<>();
//                demands.add(demand);
//                consumerCodeToDemandMap.put(demand.getConsumerCode(),demands);
//            }
//        });
//
//        if (!CollectionUtils.isEmpty(consumerCodeToDemandMap)) {
//            List<Demand> demandsToBeUpdated = new LinkedList<>();
//
//            String tenantId = getBillCriteria.getTenantId();
//
//            List<TaxPeriod> taxPeriods = mstrDataService.getTaxPeriodList(requestInfoWrapper.getRequestInfo(), tenantId);
//
//            for (String consumerCode : getBillCriteria.getConsumerCodes()) {
//                List<Demand> demands = consumerCodeToDemandMap.get(consumerCode);
//                if (CollectionUtils.isEmpty(demands))
//                    continue;
//
//                for(Demand demand : demands){
//                    if (demand.getStatus() != null
//                            && CalculatorConstants.DEMAND_CANCELLED_STATUS.equalsIgnoreCase(demand.getStatus().toString()))
//                        throw new CustomException(CalculatorConstants.EG_PT_INVALID_DEMAND_ERROR,
//                                CalculatorConstants.EG_PT_INVALID_DEMAND_ERROR_MSG);
//
//                    applytimeBasedApplicables(demand, requestInfoWrapper, timeBasedExmeptionMasterMap,taxPeriods);
//
//                    roundOffDecimalForDemand(demand, requestInfoWrapper);
//
//                    demandsToBeUpdated.add(demand);
//                }
//            }
//
//
//            /**
//             * Call demand update in bulk to update the interest or penalty
//             */
//            DemandRequest request = DemandRequest.builder().demands(demandsToBeUpdated).requestInfo(requestInfo).build();
//            StringBuilder updateDemandUrl = utils.getUpdateDemandUrl();
//            repository.fetchResult(updateDemandUrl, request);
//        }
//        return res;
//    }



//    public BillResponse getBill(RequestInfoWrapper requestInfoWrapper, String tenantId, String applicationNumber) {
//        String uri = getFetchBillURI();
//        uri = uri.replace("{1}", tenantId);
//        uri = uri.replace("{2}", applicationNumber);
//        uri = uri.replace("{3}", ndcConfiguration.getModuleCode());
//
//        Object response = repository.fetchResult(new StringBuilder(uri), requestInfoWrapper);
//        BillResponse billResponse = mapper.convertValue(response, BillResponse.class);
//
//        return billResponse;
//    }

//    public String getFetchBillURI(){
//        StringBuilder url = new StringBuilder(ndcConfiguration.getBillingHost());
//        url.append(ndcConfiguration.getFetchBillEndpoint());
//        url.append("?");
//        url.append("tenantId=");
//        url.append("{1}");
//        url.append("&");
//        url.append("consumerCode=");
//        url.append("{2}");
//        url.append("&");
//        url.append("businessService=");
//        url.append("{3}");
//
//        return url.toString();
//    }
//    /**
//     * Updates demand for the given list of calculations
//     * @param requestInfo The RequestInfo of the calculation request
//     * @param calculations List of calculation object
//     * @return Demands that are updated
//     */
//    private List<Demand> updateDemand(RequestInfo requestInfo,List<Calculation> calculations){
//        List<Demand> demands = new LinkedList<>();
//        for(Calculation calculation : calculations) {
//
//            List<Demand> searchResult = searchDemand(calculation.getTenantId(),Collections.singleton(calculation.getBpa().getApplicationNo())
//                    , requestInfo,calculation);
//
//            if(CollectionUtils.isEmpty(searchResult))
//                throw new CustomException("INVALID UPDATE","No demand exists for applicationNumber: "+calculation.getBpa().getApplicationNo());
//
//            Demand demand = searchResult.get(0);
//            List<DemandDetail> demandDetails = demand.getDemandDetails();
//            List<DemandDetail> updatedDemandDetails = getUpdatedDemandDetails(calculation,demandDetails);
//            demand.setDemandDetails(updatedDemandDetails);
//            demands.add(demand);
//        }
//         return demandRepository.updateDemand(requestInfo,demands);
//    }
//
//    /**
//     * Returns the list of new DemandDetail to be added for updating the demand
//     * @param calculation The calculation object for the update tequest
//     * @param demandDetails The list of demandDetails from the existing demand
//     * @return The list of new DemandDetails
//     */
//    private List<DemandDetail> getUpdatedDemandDetails(Calculation calculation, List<DemandDetail> demandDetails){
//
//        List<DemandDetail> newDemandDetails = new ArrayList<>();
//        Map<String, List<DemandDetail>> taxHeadToDemandDetail = new HashMap<>();
//
//        demandDetails.forEach(demandDetail -> {
//            if(!taxHeadToDemandDetail.containsKey(demandDetail.getTaxHeadMasterCode())){
//                List<DemandDetail> demandDetailList = new LinkedList<>();
//                demandDetailList.add(demandDetail);
//                taxHeadToDemandDetail.put(demandDetail.getTaxHeadMasterCode(),demandDetailList);
//            }
//            else
//              taxHeadToDemandDetail.get(demandDetail.getTaxHeadMasterCode()).add(demandDetail);
//        });
//
//        BigDecimal diffInTaxAmount;
//        List<DemandDetail> demandDetailList;
//        BigDecimal total;
//
//        for(TaxHeadEstimate taxHeadEstimate : calculation.getTaxHeadEstimates()){
//            if(!taxHeadToDemandDetail.containsKey(taxHeadEstimate.getTaxHeadCode()))
//                newDemandDetails.add(
//                        DemandDetail.builder()
//                                .taxAmount(taxHeadEstimate.getEstimateAmount())
//                                .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
//                                .tenantId(calculation.getTenantId())
//                                .collectionAmount(BigDecimal.ZERO)
//                                .build());
//            else {
//                 demandDetailList = taxHeadToDemandDetail.get(taxHeadEstimate.getTaxHeadCode());
//                 total = demandDetailList.stream().map(DemandDetail::getTaxAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//                 diffInTaxAmount = taxHeadEstimate.getEstimateAmount().subtract(total);
//                 if(diffInTaxAmount.compareTo(BigDecimal.ZERO)!=0) {
//                     newDemandDetails.add(
//                             DemandDetail.builder()
//                                     .taxAmount(diffInTaxAmount)
//                                     .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
//                                     .tenantId(calculation.getTenantId())
//                                     .collectionAmount(BigDecimal.ZERO)
//                                     .build());
//                 }
//            }
//        }
//        List<DemandDetail> combinedBillDetials = new LinkedList<>(demandDetails);
//        combinedBillDetials.addAll(newDemandDetails);
//        addRoundOffTaxHead(calculation.getTenantId(),combinedBillDetials);
//        return combinedBillDetials;
//    }
//
//    /**
//     * Searches demand for the given consumerCode and tenantIDd
//     * @param tenantId The tenantId of the tradeLicense
//     * @param consumerCodes The set of consumerCode of the demands
//     * @param requestInfo The RequestInfo of the incoming request
//     * @return Lis to demands for the given consumerCode
//     */
//    private List<Demand> searchDemand(String tenantId,Set<String> consumerCodes,RequestInfo requestInfo,Calculation calculation){
//    	String feeType = calculation.getFeeType();
//        String uri = utils.getDemandSearchURL();
//        uri = uri.replace("{1}",tenantId);
//        uri = uri.replace("{2}",(utils.getBillingBusinessService(calculation.getBpa().getBusinessService(), feeType)));
//        uri = uri.replace("{3}",StringUtils.join(consumerCodes, ','));
//
//        Object result = serviceRequestRepository.fetchResult(new StringBuilder(uri),RequestInfoWrapper.builder()
//                                                      .requestInfo(requestInfo).build());
//
//        DemandResponse response;
//        try {
//             response = mapper.convertValue(result,DemandResponse.class);
//        }
//        catch (IllegalArgumentException e){
//            throw new CustomException(BPACalculatorConstants.PARSING_ERROR,"Failed to parse response from Demand Search");
//        }
//
//        if(CollectionUtils.isEmpty(response.getDemands()))
//            return null;
//
//        else return response.getDemands();
//
//    }
//
//
//    /**
//     * Creates demand for the given list of calculations
//     * @param requestInfo The RequestInfo of the calculation request
//     * @param calculations List of calculation object
//     * @return Demands that are created
//     */
//    private List<Demand> createDemand(RequestInfo requestInfo,List<Calculation> calculations,Object mdmsData){
//        List<Demand> demands = new LinkedList<>();
//        for(Calculation calculation : calculations) {
//            BPA bpa = null;
//
//            if(calculation.getBpa()!=null) {
//            	 	bpa = calculation.getBpa();
//            } else if(calculation.getApplicationNumber()!=null) {
//            		bpa = bpaService.getBuildingPlan(requestInfo, calculation.getTenantId(), calculation.getApplicationNumber(), null);
//            }
//
//            if (bpa == null)
//                throw new CustomException(BPACalculatorConstants.INVALID_APPLICATION_NUMBER, "Demand cannot be generated for applicationNumber " +
//                        calculation.getApplicationNumber() + "  Building plan application with this number does not exist ");
//
//            String tenantId = calculation.getTenantId();
//            String consumerCode = calculation.getBpa().getApplicationNo();
//
//            User owner = bpa.getLandInfo().getOwners().get(0).toCommonUser();
//
//            List<DemandDetail> demandDetails = new LinkedList<>();
//
//            calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
//                demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
//                        .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
//                        .collectionAmount(BigDecimal.ZERO)
//                        .tenantId(tenantId)
//                        .build());
//            });
//
//
////             addRoundOffTaxHead(calculation.getTenantId(),demandDetails);
//
//            int year = Calendar.getInstance().get(Calendar.YEAR);
//            Calendar startCal = Calendar.getInstance();
//            startCal.set(Calendar.YEAR,year);
//            startCal.set(Calendar.DAY_OF_YEAR, 1);
//
//            Calendar endCal = Calendar.getInstance();
//            endCal.set(Calendar.YEAR, year);
//            endCal.set(Calendar.MONTH, 11); // 11 = december
//            endCal.set(Calendar.DAY_OF_MONTH, 31);
//
//             demands.add(Demand.builder()
//                    .consumerCode(consumerCode)
//                    .demandDetails(demandDetails)
//                    .payer(owner)
//                    .minimumAmountPayable(config.getMinimumPayableAmount())
//                    .tenantId(tenantId)
//                    .taxPeriodFrom( startCal.getTimeInMillis())
//                    .taxPeriodTo(endCal.getTimeInMillis())
//                    .consumerType("BPA")
//                    .businessService(utils.getBillingBusinessService(bpa.getBusinessService(),calculation.getFeeType()))
//                    .build());
//        }
//        return demandRepository.saveDemand(requestInfo,demands);
//    }
//
//    /**
//     * Adds roundOff taxHead if decimal values exists
//     * @param tenantId The tenantId of the demand
//     * @param demandDetails The list of demandDetail
//     */
//    private void addRoundOffTaxHead(String tenantId,List<DemandDetail> demandDetails){
//        BigDecimal totalTax = BigDecimal.ZERO;
//
//        DemandDetail prevRoundOffDemandDetail = null;
//
//        /*
//        * Sum all taxHeads except RoundOff as new roundOff will be calculated
//        * */
//        for (DemandDetail demandDetail : demandDetails){
//        	/*if(!demandDetail.getTaxHeadMasterCode().equalsIgnoreCase(BPACalculatorConstants.MDMS_ROUNDOFF_TAXHEAD))
//                totalTax = totalTax.add(demandDetail.getTaxAmount());
//            else*/
//             prevRoundOffDemandDetail = demandDetail;
//        }
//
//        BigDecimal decimalValue = totalTax.remainder(BigDecimal.ONE);
//        BigDecimal midVal = new BigDecimal(0.5);
//        BigDecimal roundOff = BigDecimal.ZERO;
//
//        /*
//        * If the decimal amount is greater than 0.5 we subtract it from 1 and put it as roundOff taxHead
//        * so as to nullify the decimal eg: If the tax is 12.64 we will add extra tax roundOff taxHead
//        * of 0.36 so that the total becomes 13
//        * */
//        if(decimalValue.compareTo(midVal) > 0)
//            roundOff = BigDecimal.ONE.subtract(decimalValue);
//
//
//        /*
//         * If the decimal amount is less than 0.5 we put negative of it as roundOff taxHead
//         * so as to nullify the decimal eg: If the tax is 12.36 we will add extra tax roundOff taxHead
//         * of -0.36 so that the total becomes 12
//         * */
//        if(decimalValue.compareTo(midVal) < 0)
//            roundOff = decimalValue.negate();
//
//        /*
//        * If roundOff already exists in previous demand create a new roundOff taxHead with roundOff amount
//        * equal to difference between them so that it will be balanced when bill is generated. eg: If the
//        * previous roundOff amount was of -0.36 and the new roundOff excluding the previous roundOff is
//        * 0.2 then the new roundOff will be created with 0.2 so that the net roundOff will be 0.2 -(-0.36)
//        * */
//     /*   if(prevRoundOffDemandDetail!=null){
//            roundOff = roundOff.subtract(prevRoundOffDemandDetail.getTaxAmount());
//        }*/
//
//        if(roundOff.compareTo(BigDecimal.ZERO)!=0){
//                 DemandDetail roundOffDemandDetail = DemandDetail.builder()
//                    .taxAmount(roundOff)
//                    .taxHeadMasterCode(BPACalculatorConstants.MDMS_ROUNDOFF_TAXHEAD)
//                    .tenantId(tenantId)
//                    .collectionAmount(BigDecimal.ZERO)
//                    .build();
//
//            demandDetails.add(roundOffDemandDetail);
//        }
//    }
    
//}
