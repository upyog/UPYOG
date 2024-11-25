package org.egov.pt.service.jobs;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Assessment;
import org.egov.pt.service.AssessmentService;
import org.egov.pt.service.PropertyService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Daily Reconciliation of pending transactions
 */
@Component
@Slf4j
public class QuarterOneBillUpdateJob implements Job {

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
   

    /**
     * Fetch live status for all pending transactions
     * except for ones which were created < 30 minutes ago, configurable value
     *
     * @param jobExecutionContext execution context with optional job parameters
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
       
    	String currentFinYear = appProperties.getFinYearStart()+"-"+appProperties.getFinYearEnd();
    	
    	//List<Assessment> assmentToSendReminder = 
    }
}
