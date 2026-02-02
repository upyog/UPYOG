package org.egov.pt.dashboardservice;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.egov.pt.models.WardwithTanent;
import org.egov.pt.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private PropertyRepository propertyRepository;

    private Map<String, String> buildMap(List<WardwithTanent> data,
                                         Function<WardwithTanent, String> valueMapper) {
        return data.stream()
                .collect(Collectors.toMap(
                        w -> w.getWardNo() + "-" + w.getTanentid(),
                        valueMapper,
                        (existing, replacement) -> existing + "," + replacement));
    }

    public Map<String, String> wardWithTanentList() {
        return buildMap(propertyRepository.getTotalapplicationwithward(),
                v -> String.valueOf(v.getCount()));
    }

    public Map<String, String> wardWithAssessment() {
        return buildMap(propertyRepository.getTotalapplicationwitAssessment(),
                v -> String.valueOf(v.getCount()));
    }

    public Map<String, String> wardWithClosedCount() {
        return buildMap(propertyRepository.getTotalapplicationwitClosed(),
                v -> String.valueOf(v.getCount()));
    }

    public Map<String, String> wardWithPaidCount() {
        return buildMap(propertyRepository.getTotalapplicationApproved(),
                v -> String.valueOf(v.getCount()));
    }

    public Map<String, String> wardWithApprovedCount() {
        return buildMap(propertyRepository.getTotalapplicationwithPaid(),
                v -> String.valueOf(v.getCount()));
    }

    public Map<String, String> wardWithMovedCount() {
        return buildMap(propertyRepository.getTotalapplicationwithMoved(),
                v -> v.getAction() + ":" + v.getCount());
    }

    public Map<String, String> wardWithPropertyRegistered() {
        return buildMap(propertyRepository.getTotalpropertyRegistered(),
                v -> v.getFinanciyalyear() + ":" + v.getCount());
    }

    public Map<String, String> wardWithPropertyAssessed() {
        return buildMap(propertyRepository.getTotalAssedproperties(),
                v -> v.getUsagecategory() + ":" + v.getCount());
    }

    public Map<String, String> wardWithTransactionCount() {
        return buildMap(propertyRepository.getTotaltransactionCount(),
                v -> v.getUsagecategory() + ":" + v.getCount());
    }

    public Map<String, String> wardWithTodaysCollection() {
        return buildMap(propertyRepository.getTotaltodaysCollection(),
                v -> v.getUsagecategory() + ":" + v.getTodaysCollection());
    }

    public Map<String, String> wardWithPropertyCount() {
        return buildMap(propertyRepository.getTotalpropertyCount(),
                v -> v.getUsagecategory() + ":" + v.getTotalpropertytaxamountpaid());
    }

    public Map<String, String> wardWithRebateGiven() {
        return buildMap(propertyRepository.getTotalrebateCollection(),
                v -> v.getUsagecategory() + ":" + v.getTodayrebategiven().negate());
    }

    public Map<String, String> wardWithPenaltyCollected() {
        return buildMap(propertyRepository.getTotalpenaltyCollection(),
                v -> v.getUsagecategory() + ":" + v.getTodaypenaltycollection());
    }

    public Map<String, String> wardWithInterestCollected() {
        return buildMap(propertyRepository.getTotalinterestCollection(),
                v -> v.getUsagecategory() + ":" + v.getTodayinterestcollection());
    }
    
    ////////////////////WITH DATE//////////////////
    
    public Map<String, String> wardWithTanentListDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalapplicationwithwardWithDate(startDate,endDate),
                v -> String.valueOf(v.getCount()));
    }
    
    public Map<String, String> wardWithAssessmentDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalapplicationwitAssessmentWithDate(startDate,endDate),
                v -> String.valueOf(v.getCount()));
    }
    
    public Map<String, String> wardWithClosedCountDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalapplicationwitClosedWithDate( startDate, endDate),
                v -> String.valueOf(v.getCount()));
    }
    
    public Map<String, String> wardWithPaidCountDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalapplicationApprovedWithDate(startDate, endDate),
                v -> String.valueOf(v.getCount()));
    }

    public Map<String, String> wardWithApprovedCountDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalapplicationwithPaidWithDate(startDate, endDate),
                v -> String.valueOf(v.getCount()));
    }
    
    public Map<String, String> wardWithMovedCountDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalapplicationwithMovedWithDate(startDate, endDate),
                v -> v.getAction() + ":" + v.getCount());
    }
    
    public Map<String, String> wardWithPropertyRegisteredDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalpropertyRegisteredWithDate(startDate, endDate),
                v -> v.getFinanciyalyear() + ":" + v.getCount());
    }
    
    public Map<String, String> wardWithPropertyAssessedDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalAssedpropertiesWithDate(startDate, endDate),
                v -> v.getUsagecategory() + ":" + v.getCount());
    }
    
    public Map<String, String> wardWithTransactionCountDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotaltransactionCountWithDate(startDate, endDate),
                v -> v.getUsagecategory() + ":" + v.getCount());
    }
    
    public Map<String, String> wardWithTodaysCollectionDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotaltodaysCollectionWithDate(startDate, endDate),
                v -> v.getUsagecategory() + ":" + v.getTodaysCollection());
    }
    
    public Map<String, String> wardWithPropertyCountDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalpropertyCountWithDate(startDate, endDate),
                v -> v.getUsagecategory() + ":" + v.getTotalpropertytaxamountpaid());
    }
    
    public Map<String, String> wardWithRebateGivenDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalrebateCollectionWithDate(startDate, endDate),
                v -> v.getUsagecategory() + ":" + v.getTodayrebategiven().negate());
    }
    
    public Map<String, String> wardWithPenaltyCollectedDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalpenaltyCollectionWithDate(startDate, endDate),
                v -> v.getUsagecategory() + ":" + v.getTodaypenaltycollection());
    }

    public Map<String, String> wardWithInterestCollectedDate(Long startDate,Long endDate) {
        return buildMap(propertyRepository.getTotalinterestCollectionWithDate(startDate, endDate),
                v -> v.getUsagecategory() + ":" + v.getTodayinterestcollection());
    }
    /////////////////////////////////////DATE////////////////////////////////////////////////
}
