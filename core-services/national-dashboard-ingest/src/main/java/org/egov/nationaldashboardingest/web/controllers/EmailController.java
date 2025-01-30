package org.egov.nationaldashboardingest.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.service.StateListDB;
import org.egov.nationaldashboardingest.utils.EmailUtil;
import org.egov.nationaldashboardingest.utils.ExcelUtil;
import org.egov.nationaldashboardingest.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.egov.nationaldashboardingest.service.StateFetchService;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private ExcelUtil excelUtil;


    @RequestMapping(value = "/_sendEmail", method = RequestMethod.POST)
    public void sendEmailtoMissingState(@RequestBody RequestInfoWrapper requestInfoWrapper, HttpServletResponse response) throws IOException {
        RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
        log.info("Received RequestInfo: {}", requestInfo);
        Map<String,Map<String , Object>> responseList = stateFetch.stateList(requestInfo);

        emailUtil.sendEmail(requestInfo, responseList);
    }
}
