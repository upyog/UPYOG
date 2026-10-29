package org.egov.echallan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.Challan;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.repository.ServiceRequestRepository;
import org.egov.echallan.web.models.calculation.Calculation;
import org.egov.echallan.web.models.calculation.CalculationReq;
import org.egov.echallan.web.models.calculation.CalculationRes;
import org.egov.echallan.web.models.calculation.CalulationCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Service
public class CalculationService {


    private ServiceRequestRepository serviceRequestRepository;

    private ObjectMapper mapper;

    private ChallanConfiguration config;
    
    @Autowired
    public CalculationService(ServiceRequestRepository serviceRequestRepository, ObjectMapper mapper,ChallanConfiguration config) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.mapper = mapper;
        this.config = config;
    }

    public Challan addCalculation(ChallanRequest request){
        RequestInfo requestInfo = request.getRequestInfo();
        Challan challan = request.getChallan();

        if(challan==null)
            throw new CustomException("INVALID REQUEST","The request for calculation cannot be empty or null");

        CalculationRes response = getCalculation(requestInfo,challan);
        List<Calculation> calculations = response.getCalculations();
        Map<String,Calculation> applicationNumberToCalculation = new HashMap<>();
        calculations.forEach(calculation -> {
            applicationNumberToCalculation.put(calculation.getChallan().getChallanNo(),calculation);
            // Populate challanNo in calculation before removing challan object
            calculation.setChallanNo(calculation.getChallan().getChallanNo());
            calculation.setChallan(null);
        });

        Calculation calculation = applicationNumberToCalculation.get(challan.getChallanNo());
        if(calculation != null) {
            // Ensure challanNo is set
            calculation.setChallanNo(challan.getChallanNo());
            challan.setCalculation(calculation);
        }

        return challan;
    }

    private CalculationRes getCalculation(RequestInfo requestInfo,Challan challan){
    	
    	StringBuilder uri = new StringBuilder();
        uri.append(config.getCalculatorHost());
        uri.append(config.getCalculateEndpoint());
        List<CalulationCriteria> criterias = new LinkedList<>();

        CalulationCriteria criteria = CalulationCriteria.builder()
                .challan(challan)
                .challanNo(challan.getChallanNo())
                .tenantId(challan.getTenantId())
                .build();
        criterias.add(criteria);

        CalculationReq request = CalculationReq.builder().calulationCriteria(criterias)
                .requestInfo(requestInfo)
                .build();

        Object result = serviceRequestRepository.fetchResult(uri,request);
        CalculationRes response = null;
        try{
            response = mapper.convertValue(result,CalculationRes.class);
        }
        catch (IllegalArgumentException e){
            throw new CustomException("PARSING ERROR","Failed to parse response of calculate");
        } 
        return response;
    }

    /**
     * Updates existing demand with fee waiver applied
     * Calculator will find demand using consumerCode (challanNo) if demandId is not provided
     * 
     * @param request ChallanRequest containing challan with fee waiver in additionalDetail
     * @param demandId Optional demand ID (if null, calculator will find using consumerCode)
     * @return Challan with updated calculation
     */
    public Challan updateCalculation(ChallanRequest request, String demandId){
        RequestInfo requestInfo = request.getRequestInfo();
        Challan challan = request.getChallan();

        if(challan==null)
            throw new CustomException("INVALID REQUEST","The request for calculation update cannot be empty or null");

        CalculationRes response = updateCalculation(requestInfo, challan, demandId);
        List<Calculation> calculations = response.getCalculations();
        Map<String,Calculation> applicationNumberToCalculation = new HashMap<>();
        calculations.forEach(calculation -> {
            applicationNumberToCalculation.put(calculation.getChallan().getChallanNo(),calculation);
            // Populate challanNo in calculation before removing challan object
            calculation.setChallanNo(calculation.getChallan().getChallanNo());
            calculation.setChallan(null);
        });

        // Store updated calculation in challan
        Calculation calculation = applicationNumberToCalculation.get(challan.getChallanNo());
        if(calculation != null) {
            // Ensure challanNo is set
            calculation.setChallanNo(challan.getChallanNo());
            challan.setCalculation(calculation);
        }

        return challan;
    }

    /**
     * Calls calculator service to update existing demand
     * Calculator will find demand using consumerCode if demandId is not provided
     * 
     * @param requestInfo RequestInfo object
     * @param challan Challan with fee waiver in additionalDetail
     * @param demandId Optional demand ID (calculator can find using consumerCode)
     * @return CalculationRes with updated calculation
     */
    private CalculationRes updateCalculation(RequestInfo requestInfo, Challan challan, String demandId){
    	
    	StringBuilder uri = new StringBuilder();
        uri.append(config.getCalculatorHost());
        uri.append(config.getUpdateCalculateEndpoint());
        List<CalulationCriteria> criterias = new LinkedList<>();

        // Create criteria - calculator will find demand using consumerCode (challanNo) if demandId is null
        CalulationCriteria criteria = CalulationCriteria.builder()
                .challan(challan)
                .challanNo(challan.getChallanNo())
                .tenantId(challan.getTenantId())
                .demandId(demandId)  // Optional - calculator can find using consumerCode
                .build();
        
        criterias.add(criteria);

        CalculationReq updateRequest = CalculationReq.builder()
                .calulationCriteria(criterias)
                .requestInfo(requestInfo)
                .build();

        Object result = serviceRequestRepository.fetchResult(uri, updateRequest);
        CalculationRes response = null;
        try{
            response = mapper.convertValue(result,CalculationRes.class);
        }
        catch (IllegalArgumentException e){
            throw new CustomException("PARSING ERROR","Failed to parse response of update calculation");
        } 
        return response;
    }

}
