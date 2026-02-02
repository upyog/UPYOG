package org.egov.pt.service.jobs;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Assessment;
import org.egov.pt.models.AssessmentSearchCriteria;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.collection.BillResponse;
import org.egov.pt.models.enums.Status;
import org.egov.pt.service.AssessmentService;
import org.egov.pt.service.BillingService;
import org.egov.pt.service.NotificationService;
import org.egov.pt.service.PropertyService;
import org.egov.pt.util.PropertyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Daily Reconciliation of pending transactions
 */
@Component
@Slf4j
public class DailyBillUpdateJob implements Job {

    private static RequestInfo requestInfo;

    @PostConstruct
    public void init() {
        User userInfo = User.builder()
                .uuid(appProperties.getSystemUser())
                .type("SYSTEM")
                .roles(Collections.emptyList()).id(0L).build();

        requestInfo = new RequestInfo();
        requestInfo.setUserInfo(userInfo);
    //("", "", 0L, "", "", "", "", "", "", userInfo);
    }

    @Autowired
    private PropertyConfiguration appProperties;
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
   private  AssessmentService assessmentService;
    
    @Autowired
    PropertyUtil propertyUtil;
    
    
    @Autowired
    BillingService billService;
    
    @Autowired
    private NotificationService notification;
   

    /**
     * Fetch live status for all pending transactions
     * except for ones which were created < 30 minutes ago, configurable value
     *
     * @param jobExecutionContext execution context with optional job parameters
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
       
    	String currentFinYear = appProperties.getFinYearStart()+"-"+appProperties.getFinYearEnd().toString().substring(2);
    	BillResponse billResponse = null;
    	AssessmentSearchCriteria criteria = new AssessmentSearchCriteria();
    	criteria.setFinancialYear(currentFinYear);
    	criteria.setStatus(Status.ACTIVE);
    	List<Assessment> a = assessmentService.searchAssessments(criteria, requestInfo);
    	
    	for(Assessment asmt :a) {
    		 billResponse =   billService.fetchBillForDailyBillUpdate(asmt.getPropertyId(), requestInfo,asmt.getTenantId(), asmt.getModeOfPayment());
    		if(null!=billResponse && null!=billResponse.getBill() && !billResponse.getBill().isEmpty()) {
    			Long expiryDate= billResponse.getBill().get(0).getBillDetails().get(0).getExpiryDate();
    			List<OwnerInfo> owner = asmt.getOwners();
    			//ForEach Owner create a sms and event 
    			LocalDate endDate =Instant.ofEpochMilli(expiryDate).atZone(ZoneId.systemDefault()).toLocalDate();
    			LocalDate startDate = LocalDate.now();
    			int daysdiff=(int) ChronoUnit.DAYS.between(startDate,endDate);
    			if(daysdiff<=5 && daysdiff>0)
    			{
    				System.out.println("endDate::"+endDate);
    				System.out.println("daysdiff::"+daysdiff);
    				notification.sendPaymentReminder(asmt, requestInfo);
    			}
    		}
    	}
    	System.out.println("HI I am Executing through CRON:::"+currentFinYear);
    	
    	//List<Assessment> assmentToSendReminder = 
    }
}
