package org.egov.garbageservice.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.model.EgGrbgBillTracker;
import org.egov.garbageservice.model.SmsTracker;
import org.egov.garbageservice.repository.EgGrbgBillTrackerRepository;
import org.egov.garbageservice.repository.SmsTrackerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.egov.garbageservice.repository.BillSmsView;


import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GarbageSmsService {

    @Autowired
    private EgGrbgBillTrackerRepository billTrackerRepository;

    @Autowired
    private SmsTrackerRepository smsTrackerRepository;

    /**
     * Fetch pending bills from eg_grbg_bill_tracker
     * and insert into sms_tracker
     */
    @Transactional
    public void addPendingBillsToSmsTracker() {

    	List<BillSmsView> pendingBills =
    	        billTrackerRepository.fetchActiveBillsForSms(100, 0);

        if (pendingBills == null || pendingBills.isEmpty()) {
            log.info("No pending garbage bills found for SMS processing");
            return;
        }

        for (BillSmsView bill : pendingBills) {
            try {
                SmsTracker sms = new SmsTracker();
                String mobile = bill.getMobileNumber(); 
                
                sms.setUuid(UUID.randomUUID().toString());
                sms.setMobileNumber(mobile);
                sms.setTenantId(bill.getTenantId());
                sms.setAmount(bill.getGrbgBillAmount());
                sms.setApplicationNo(bill.getGrbgApplicationId());
                sms.setService("GARBAGE");
                sms.setMonth(bill.getMonth());
                sms.setYear(bill.getYear());
                
                String smsJson = "{"
                        + "\"mobileNumber\":\"" + bill.getMobileNumber() + "\","
                        + "\"message\":\"" + generateMessage(bill) + "\""
                        + "}";
                String additionalDetail = "{"
                	    + "\"billId\":\"" + bill.getBillId() + "\","
                	    + "\"source\":\"GARBAGE_SERVICE\""
                	    + "}";                sms.setSmsRequest(smsJson);
                
                smsTrackerRepository.insertSmsTracker(
                	    UUID.randomUUID().toString(),
                	    bill.getTenantId(),
                	    bill.getGrbgBillAmount(),
                	    bill.getGrbgApplicationId(),
                	    "GARBAGE",
                	    bill.getMonth(),
                	    bill.getYear(),
                	    bill.getFromDate(),
                	    bill.getToDate(),
                	    null,                     // financialYear
                	    "SYSTEM",                 // createdBy
                	    System.currentTimeMillis(), // createdTime
                	    bill.getLastModifiedBy(),    // lastModifiedBy
                	    System.currentTimeMillis(), // lastModifiedTime
                	    bill.getWard(),                        // ward
                	    bill.getBillId(),         // billId
                	    additionalDetail,         // additionalDetail
                	    bill.getMobileNumber(),   // mobileNumber
                	    bill.getOwnerName(),                   // ownerName
                	    smsJson,                  // smsRequest
                	    "{}",                     // smsResponse
                	    false                     // smsStatus
                	);

                log.info(
                    "SMS tracker entry created for applicationNo={}",
                    bill.getGrbgApplicationId()
                );

            } catch (Exception e) {
                log.error(
                    "Failed to process SMS for applicationNo={}",
                    bill.getGrbgApplicationId(),
                    e
                );
            }
        }
    }

    private String generateMessage(BillSmsView  bill) {
        return String.format(
            "Your garbage bill %s for the period %s/%s amounting to Rs %.2f "
                + "has been generated on CitizenSeva portal. Please pay online.",
            bill.getGrbgApplicationId(),
            bill.getMonth(),
            bill.getYear(),
            bill.getGrbgBillAmount() != null
                ? bill.getGrbgBillAmount().doubleValue()
                : 0.0
        );
    }
}
