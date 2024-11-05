package org.egov.nationaldashboardingest.service;


import org.egov.nationaldashboardingest.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
@Service
public class EmailService {

    @Autowired
    private EmailUtil emailUtil;
    
    public void sendEmails(Map<String, Map<String, String>> missingStates) {

        emailUtil.sendEmail(missingStates);
    }
}
