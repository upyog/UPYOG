package org.egov.nationaldashboardingest.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.service.EmailService;
import org.egov.nationaldashboardingest.service.StateListDB;
import org.egov.nationaldashboardingest.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.egov.nationaldashboardingest.service.StateFetchService;


import java.util.*;

@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private StateFetchService stateFetch;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private StateListDB stateListDB;

    @Autowired
    private EmailService emailService;


    @RequestMapping(value = "/_sendEmail", method = RequestMethod.POST)
    public ResponseEntity<String> sendEmailtoMissingState(@RequestBody RequestInfo requestInfo) {
        Map<String, Map<String,String>> missingStates = new HashMap<>();

        Map<String,Map<String , String>> responseList = stateFetch.stateList(requestInfo);
        Set<String> dbState = stateListDB.findifrecordexists();

        for (Map.Entry<String,Map<String, String>> entry : responseList.entrySet()) {
            String stateCode = entry.getKey();
            Map<String, String> officerInfo = entry.getValue();
            if (!dbState.contains(stateCode)) {
                missingStates.put(stateCode,officerInfo);
            }
        }
        emailUtil.sendEmail(missingStates);

        return ResponseEntity.ok("Emails sent successfully");
    }


}
