package org.egov.wf.web.controllers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.wf.service.WorkflowService;
import org.egov.wf.util.ResponseInfoFactory;
import org.egov.wf.web.models.ProcessInstance;
import org.egov.wf.web.models.ProcessInstanceRequest;
import org.egov.wf.web.models.ProcessInstanceResponse;
import org.egov.wf.web.models.ProcessInstanceSearchCriteria;
import org.egov.wf.web.models.RequestInfoWrapper;
import org.egov.wf.web.models.StatusCountRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/egov-wf")
public class WorkflowController {


    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final WorkflowService workflowService;

    private final ResponseInfoFactory responseInfoFactory;
    
    @Autowired
    private final RestTemplate restTemplate;

    @Value("${egov.user.host}")
	private String authServiceHost;
    
    @Value("${egov.user.details.endpoint}")
	private String authServiceUri;

    @Autowired
    public WorkflowController(ObjectMapper objectMapper, HttpServletRequest request,
                              WorkflowService workflowService, ResponseInfoFactory responseInfoFactory,RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.workflowService = workflowService;
        this.responseInfoFactory = responseInfoFactory;
        this.restTemplate=restTemplate;
    }



        @RequestMapping(value="/process/_transition", method = RequestMethod.POST)
        public ResponseEntity<ProcessInstanceResponse> processTransition(@Valid @RequestBody ProcessInstanceRequest processInstanceRequest) {
                List<ProcessInstance> processInstances =  workflowService.transition(processInstanceRequest);
                ProcessInstanceResponse response = ProcessInstanceResponse.builder().processInstances(processInstances)
                        .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(processInstanceRequest.getRequestInfo(), true))
                        .build();
                return new ResponseEntity<>(response,HttpStatus.OK);
        }




        @RequestMapping(value="/process/_search", method = RequestMethod.POST)
        public ResponseEntity<ProcessInstanceResponse> search( @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                              @Valid @ModelAttribute ProcessInstanceSearchCriteria criteria) {
        	if(null==requestInfoWrapper.getRequestInfo().getUserInfo()) {
        		String authToken=requestInfoWrapper.getRequestInfo().getAuthToken();
        		String authURL = String.format("%s%s", authServiceHost, authServiceUri);
        		final HttpHeaders headers = new HttpHeaders();
        		headers.add("access_token", authToken);
        		User user=restTemplate.postForObject(authURL, headers, User.class);
        		System.out.println(user);
        		org.egov.common.contract.request.User u = new org.egov.common.contract.request.User();
        		u.setEmailId(user.getEmailId());
        		u.setId(user.getId());
        		u.setMobileNumber(user.getMobileNumber());
        		u.setName(user.getName());
        		List<Role> r = new ArrayList<>();
        		for(Role rl : user.getRoles()) {
        			Role rln = new Role();
        			rln.setCode(rl.getCode());
        			//rln.setId(rl.getCode());
        		
        			rln.setName(rl.getName());
        			rln.setTenantId(rl.getTenantId());
        			r.add(rln);
        		}
        		u.setRoles(r);
        		u.setType(user.getType());
        		requestInfoWrapper.getRequestInfo().setUserInfo(u);
        		String userid=user.getId().toString();
        		List<String> userids = new ArrayList<String>(Arrays.asList(userid));
        		
        	}
        List<ProcessInstance> processInstances = workflowService.search(requestInfoWrapper.getRequestInfo(),criteria);
        Integer count = workflowService.getUserBasedProcessInstancesCount(requestInfoWrapper.getRequestInfo(),criteria);
            ProcessInstanceResponse response  = ProcessInstanceResponse.builder().processInstances(processInstances).totalCount(count).build();
                return new ResponseEntity<>(response,HttpStatus.OK);
        }

    /**
     * Returns the count of records matching the given criteria
     * @param requestInfoWrapper
     * @param criteria
     * @return
     */
    @RequestMapping(value="/process/_count", method = RequestMethod.POST)
        public ResponseEntity<Integer> count(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                              @Valid @ModelAttribute ProcessInstanceSearchCriteria criteria) {
    		criteria.setIsNearingSlaCount(Boolean.FALSE);
            Integer count = workflowService.count(requestInfoWrapper.getRequestInfo(),criteria);
            return new ResponseEntity<>(count,HttpStatus.OK);
        }

    @RequestMapping(value="/escalate/_search", method = RequestMethod.POST)
    public ResponseEntity<ProcessInstanceResponse> searchEscalatedApplications(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                          @Valid @ModelAttribute ProcessInstanceSearchCriteria criteria) {
        List<ProcessInstance> processInstances = workflowService.escalatedApplicationsSearch(requestInfoWrapper.getRequestInfo(),criteria);
        Integer count = workflowService.countEscalatedApplications(requestInfoWrapper.getRequestInfo(),criteria);
        ProcessInstanceResponse response  = ProcessInstanceResponse.builder().processInstances(processInstances).totalCount(count)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * Returns the count of each status of records matching the given criteria
     * @param requestInfoWrapper
     * @param criteria
     * @return
     */
    @RequestMapping(value = "/process/_statuscount", method = RequestMethod.POST)
    public ResponseEntity<List> StatusCount(@Valid @RequestBody StatusCountRequest statusCountRequest,
            @Valid @ModelAttribute ProcessInstanceSearchCriteria criteria) {
        ProcessInstanceSearchCriteria statusCriteria = statusCountRequest.getProcessInstanceSearchCriteria();
        if (statusCriteria == null) {
            statusCriteria = criteria;
        }
        List result = workflowService.statusCount(statusCountRequest.getRequestInfo(), statusCriteria);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @RequestMapping(value="/process/_nearingslacount", method = RequestMethod.POST)
    public ResponseEntity<Integer> nearingSlaCount(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                         @Valid @ModelAttribute ProcessInstanceSearchCriteria criteria) {
        criteria.setIsNearingSlaCount(Boolean.TRUE);
        Integer count = workflowService.count(requestInfoWrapper.getRequestInfo(),criteria);
        return new ResponseEntity<>(count,HttpStatus.OK);
    }

}
