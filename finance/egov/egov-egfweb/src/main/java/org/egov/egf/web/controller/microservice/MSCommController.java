package org.egov.egf.web.controller.microservice;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.models.RequestInfoWrapper;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.web.support.ui.Inbox;
import org.egov.infra.workflow.entity.StateAware;
import org.egov.infra.workflow.inbox.InboxRenderServiceDelegate;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.WorkflowService;
import org.egov.infra.validation.SanitizeHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

@Controller
@Validated
public class MSCommController {
	
	private static final String SELECT = "Select";

	private static final Logger LOGGER = LoggerFactory.getLogger(MSCommController.class);

    @Autowired
    MicroserviceUtils microserviceUtils;
    
    @Autowired
    RedisIndexedSessionRepository redisRepository;
    
    @Autowired
    private WorkflowService<StateAware> workflowService;

    @Autowired
    private InboxRenderServiceDelegate<StateAware> inboxRenderServiceDelegate;

    @GetMapping(value = "/depratments")
    @ResponseBody
    public List<Department> getDetapartments() {
        return microserviceUtils.getDepartments();
    }

	/*
	 * Spring 6 / Hibernate 6 Migration Fix:
	 * 1. Added null-safety check for `params` and `departmentRule` parameter.
	 *    Calling `.trim()` directly on `params.get("departmentRule")` without null check threw a `NullPointerException`
	 *    when departmentRule was missing/empty, returning HTTP 400 / 500 Bad Request ("json fail").
	 * 2. Added null verification for `wfmatrix` and `microserviceUtils.getDesignations()` to prevent unhandled NPEs
	 *    during workflow designation filtering.
	 */
	@GetMapping(value = "/designations")
	@ResponseBody
	public List<Designation> getDesignations(@RequestParam final Map<String, String> params) {
		final List<String> workflowDesignations = new ArrayList<>();
		final String departmentRule = params != null ? params.get("departmentRule") : null;
		if (departmentRule != null && !SELECT.equalsIgnoreCase(departmentRule.trim())) {
			final WorkFlowMatrix wfmatrix = workflowService.getWfMatrix(
					params.get("type"),
					departmentRule.trim(),
					null,
					params.get("additionalRule"),
					params.get("currentState"),
					params.get("pendingAction")
			);
			if (wfmatrix != null && wfmatrix.getCurrentDesignation() != null) {
				workflowDesignations.addAll(Arrays.asList(wfmatrix.getCurrentDesignation().split(",")));
			}
			final List<Designation> allDesignations = microserviceUtils.getDesignations();
			if (allDesignations != null) {
				return allDesignations.stream()
						.filter(desig -> desig != null && desig.getName() != null && workflowDesignations.contains(desig.getName()))
						.collect(Collectors.toList());
			}
		}
		return Collections.emptyList();
	}

	@GetMapping(value = "/approvers/{deptId}/{desgId}")
	@ResponseBody
	public List<EmployeeInfo> getApprovers(@PathVariable(name = "deptId") @SanitizeHtml String deptId,
			@PathVariable(name = "desgId") @SanitizeHtml String desgnId) {
		return microserviceUtils.getApprovers(deptId, desgnId);
	}

    @PostMapping(value = "/rest/ClearToken")
    @ResponseBody
    public ResponseEntity<Object> logout(@RequestBody RequestInfoWrapper request,HttpServletRequest httpReq) {
        try {
            String accessToken = request.getRequestInfo().getAuthToken();
            String sessionId = httpReq.getSession().getId();
            if(sessionId!=null && !sessionId.equalsIgnoreCase("null")){
				LOGGER.info("********* Retrieved session::authtoken******** {}::{}", sessionId, accessToken);
                if(redisRepository!=null){
                	LOGGER.info("*********** Deleting the session for redisrepository {}", sessionId);   
                    microserviceUtils.removeSessionFromRedis(accessToken, sessionId);
                }
            }

        } catch (HttpClientErrorException ex) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/rest/refreshToken")
    @ResponseBody
    public ResponseEntity<Object> refreshToken(@RequestParam(value = "oldToken") @SanitizeHtml String oldToken,
            @RequestParam(value = "newToken") @SanitizeHtml String newToken) {

        try {
            if (null != oldToken && null != newToken) {
                microserviceUtils.refreshToken(oldToken, newToken);
            }
        } catch (HttpClientErrorException ex) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "inbox/items", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Inbox> showInbox() {

        return inboxRenderServiceDelegate.getCurrentUserInboxItems();
    }

    @GetMapping(value = "inbox/history", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<Inbox> showInboxHistory(@RequestParam("stateId") Long stateId) {
        return inboxRenderServiceDelegate.getWorkflowHistoryItems(stateId);
    }
}
