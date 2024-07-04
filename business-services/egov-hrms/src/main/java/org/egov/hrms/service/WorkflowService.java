package org.egov.hrms.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.hrms.config.PropertiesManager;
import org.egov.hrms.repository.RestCallRepository;
import org.egov.hrms.utils.HRMSConstants;
import org.egov.hrms.web.contract.ProcessInstance;
import org.egov.hrms.web.contract.ProcessInstanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {


	@Autowired
	private RestCallRepository restCallRepository;
	
	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
//	String  finalUserTenantId1 =null;
	
	public Object workflowTransition(ProcessInstanceRequest processInstanceRequest) {
		
		// validate request
		validateProcessInstances(processInstanceRequest);
		

		// trigger workflow
		LinkedHashMap responseMap = null;
		StringBuilder uri = new StringBuilder(propertiesManager.getWfHost());
		uri.append(propertiesManager.getWfTransition());
		try {
			responseMap = (LinkedHashMap) restCallRepository.fetchResult(uri, processInstanceRequest);
			
			workflowTransitionPostProcess(processInstanceRequest);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseMap;
	}

	private void workflowTransitionPostProcess(ProcessInstanceRequest processInstanceRequest) {
		
		processInstanceRequest.getProcessInstances().stream().forEach(instance -> {
			
			// update TL status
			if(StringUtils.equalsIgnoreCase(instance.getBusinessService(), HRMSConstants.TRADELICENCE_WORKFLOW_BUSINESS_SERVICE_NEWTL)
					&& StringUtils.equalsIgnoreCase(instance.getModuleName(), HRMSConstants.TRADELICENCE_WORKFLOW_MODULE_NAME)) {
				updateTlStatus(instance);
			}
			
		});
		
	}

	private void updateTlStatus(ProcessInstance instance) {
		
		if(StringUtils.equalsIgnoreCase(instance.getAction(), HRMSConstants.TRADELICENCE_WORKFLOW_NEWTL_ACTION_VERIFY)) {
			updateTlStatus(instance.getBusinessId(), HRMSConstants.TRADELICENCE_NEWTL_APPLICATION_STATUS_VERIFIED);
		}else if(StringUtils.equalsIgnoreCase(instance.getAction(), HRMSConstants.TRADELICENCE_WORKFLOW_NEWTL_ACTION_APPROVE)) {
			updateTlStatus(instance.getBusinessId(), HRMSConstants.TRADELICENCE_NEWTL_APPLICATION_STATUS_APPROVED);
		}
		
	}

	public void updateTlStatus(String businessId, String action) {

		Map<String, Object> inputs = new HashMap<>();
		inputs.put("status", action);
		inputs.put("applicationNumber", businessId);

		String updateQuery = "UPDATE eg_tl_tradelicense set status =:status WHERE applicationnumber =:applicationNumber ";

		namedParameterJdbcTemplate.update(updateQuery, inputs);

	}

//	private void updateTlStatus(String businessId, String string) {
//		
//		public static final String UPDATE_QUERY = "UPDATE grbg_collection_unit " +
//                "SET unit_name = ?, unit_ward = ?, ulb_name = ?, type_of_ulb = ? " +
//                "WHERE uuid = ?, is_active = ?";
//		jdbcTemplate.update("UPDATE grbg_collection_unit " +
//                "SET unit_name = ?, unit_ward = ?, ulb_name = ?, type_of_ulb = ? " +
//                "WHERE uuid = ?, is_active = ?",
//                grbgCollectionUnit.getUuid(),
//                grbgCollectionUnit.getUnitName(),
//                grbgCollectionUnit.getUnitWard(),
//                grbgCollectionUnit.getUlbName(),
//                grbgCollectionUnit.getTypeOfUlb());
//		
//	}

	private void validateProcessInstances(ProcessInstanceRequest processInstanceRequest) {
	    String finalInspectorUserTenantId = null;
	    String finalSuperintendentUserTenantId = null;

	    if (processInstanceRequest != null
	            && processInstanceRequest.getRequestInfo() != null
	            && processInstanceRequest.getRequestInfo().getUserInfo() != null
	            && processInstanceRequest.getRequestInfo().getUserInfo().getRoles() != null) {

	        List<org.egov.common.contract.request.Role> roles = processInstanceRequest.getRequestInfo().getUserInfo().getRoles();
	        for (org.egov.common.contract.request.Role role : roles) {
	            if (StringUtils.equalsIgnoreCase(role.getCode(), "TL_FIELD_INSPECTOR")) {
	                finalInspectorUserTenantId = role.getTenantId();
	                break; // Assuming TL_FIELD_INSPECTOR role is unique per request
	            }
	        }
	        for (org.egov.common.contract.request.Role role : roles) {
	            if (StringUtils.equalsIgnoreCase(role.getCode(), "TL_FIELD_SUPERINTENDENT")) {
	            	finalSuperintendentUserTenantId = role.getTenantId();
	                break; // Assuming TL_FIELD_INSPECTOR role is unique per request
	            }
	        }

	    } else {
	        throw new RuntimeException("Provide correct Process Instance Request.");
	    }

	    final String userInspectorTenantId = finalInspectorUserTenantId;
	    final String userSuperintendentTenantId = finalSuperintendentUserTenantId;

	    processInstanceRequest.getProcessInstances().forEach(instance -> {
	        if (StringUtils.equalsIgnoreCase(instance.getBusinessService(), "NewTL")
	                && StringUtils.equalsIgnoreCase(instance.getAction(), "VERIFY")
	                && !StringUtils.equalsIgnoreCase(instance.getTenantId(), userInspectorTenantId)) {
	            throw new RuntimeException("Inspector can only verify its own ULB TL applications.");
	        }
	        if (StringUtils.equalsIgnoreCase(instance.getBusinessService(), "NewTL")
	                && StringUtils.equalsIgnoreCase(instance.getAction(), "APPROVE")
	                && !StringUtils.equalsIgnoreCase(instance.getTenantId(), userSuperintendentTenantId)) {
	            throw new RuntimeException("Superintendent can only approve its own ULB TL applications.");
	        }
	    });
	}

	
	
//	private void validateProcessInstances1(ProcessInstanceRequest processInstanceRequest) {
//		
////		Role role = null;
////		String userTenantId = null;
//		String  finalUserTenantId =null;
////		List<ProcessInstance> processInstances = new ArrayList<>();
//		if(null != processInstanceRequest 
//				&& null != processInstanceRequest.getRequestInfo()
//				&& null != processInstanceRequest.getRequestInfo().getUserInfo()
//				&& null != processInstanceRequest.getRequestInfo().getUserInfo().getRoles()) {
//			
//			List<org.egov.common.contract.request.Role> roles = processInstanceRequest.getRequestInfo().getUserInfo().getRoles();
//			roles.stream().forEach(role4 -> {
//				if(StringUtils.equalsIgnoreCase(role4.getCode(), "TL_FIELD_INSPECTOR")) {
//					finalUserTenantId = role4.getTenantId();
//				}
//			});
//					
////					.filter(role1 -> 
////			StringUtils.equalsIgnoreCase(role.getCode(), "TL_FIELD_INSPECTOR")).findFirst().orElse(null);
////			if(null != role2) {
////				
////			}
//			
////			role.add();
////			userTenantId = processInstanceRequest.getRequestInfo().getUserInfo().getTenantId();
//		}else {
//			throw new RuntimeException("Provide correct Process Instance Request.");
//		}
//		
//		 //userTenantId;
////		final LinkedHashMap responseMap1 = responseMap;
//		
//		processInstanceRequest.getProcessInstances().stream().forEach(instance -> {
//			
//			if((StringUtils.equalsIgnoreCase(instance.getBusinessService(), "TRADELICENSE")
//					&& StringUtils.equalsIgnoreCase(instance.getAction(), "VERIFY"))
//					&& !StringUtils.equalsIgnoreCase(instance.getTenantId(), finalUserTenantId)) {
//				throw new RuntimeException("Inspector can only verify its own ULB TL applications.");
////				processInstances.add(instance);
//			}
//			
//		});
//		
////		processInstanceRequest.setProcessInstances(processInstances);
//	}
}
