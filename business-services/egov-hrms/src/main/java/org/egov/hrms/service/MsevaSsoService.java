package org.egov.hrms.service;


import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.hrms.config.PropertiesManager;
import org.egov.hrms.model.Assignment;
import org.egov.hrms.model.Employee;
import org.egov.hrms.model.Jurisdiction;
import org.egov.hrms.model.JurisdictionRole;
import org.egov.hrms.model.Role;
import org.egov.hrms.model.enums.GuardianRelation;
import org.egov.hrms.repository.RestCallRepository;
import org.egov.hrms.utils.HRMSConstants;
import org.egov.hrms.utils.MsevaSsoConstants;
import org.egov.hrms.web.contract.AuthenticateUserInputRequest;
import org.egov.hrms.web.contract.EmployeeRequest;
import org.egov.hrms.web.contract.EmployeeResponse;
import org.egov.hrms.web.contract.EmployeeSearchCriteria;

//import org.egov.hrms.web.contract.RequestInfo;
import org.egov.hrms.web.contract.RequestInfoWrapper;
//import org.egov.hrms.web.contract.User;
//import org.egov.common.contract.request.User;

import org.egov.hrms.web.contract.UserInfo;
import org.egov.hrms.web.validator.EmployeeValidator;
import org.egov.mdms.model.MdmsResponse;
//import org.hibernate.validator.internal.util.logging.Log_.logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Slf4j
@Service
public class MsevaSsoService {
	
	@Autowired
	private PropertiesManager propertiesManager;
	
	@Autowired
	private RestCallRepository restCallRepository;
	
	@Autowired
	private HRMSConstants hRMSConstants;
	
	@Autowired
	private MsevaSsoConstants msevaSsoConstants;
	
	@Autowired
	private EmployeeService employeeService; //for the internal API

	@Autowired
	private EmployeeValidator validator; 
	
	@Autowired
	private MDMSService mdmsService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	
	private int ReadValuesFromApi(String decpData) {
	    int respValue = 0;

	    StringBuilder uri = new StringBuilder();
	    uri.append(propertiesManager.hrmsEsevaApiHost).append(propertiesManager.hrmsEsevaApiEndPoint); // Construct URL for HRMS Eseva call

	    Map<String, Object> apiRequest = new HashMap<>();
	    apiRequest.put(propertiesManager.hrmsEsevaApiRequestObject, decpData); //hrmsEsevaApiRequestObject

	    try {
	        Map<String, Object> responseMap = (Map<String, Object>) restCallRepository.fetchResult(uri, apiRequest);
	        
	        if(responseMap != null) {
	        	Object responseObj = responseMap.get(propertiesManager.hrmsEsevaApiResponseObjectResp); //hrmsEsevaApiResponseObjectResp
	        	respValue = Integer.parseInt(responseObj.toString());
	        	if (respValue == 0) {
                    return respValue;
                }
	        }
	        if (responseMap != null && responseMap.containsKey(propertiesManager.hrmsEsevaApiResponseObjectData)) {
	            List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseMap.get(propertiesManager.hrmsEsevaApiResponseObjectData);
	            if (!dataList.isEmpty()) {
	                Map<String, Object> dataObject = dataList.get(0);
	                String responseVal = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectResp);
	                respValue = Integer.parseInt(responseVal);
	                String username = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectUsername);
	                username = username.replaceAll("[^a-zA-Z0-9\\s]", "");
	                String mobno = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectMobile);
	                String distname = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectDistrict);
	                String email = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectEmail);
	                String fullname = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectFullname);
	                fullname = fullname.replaceAll("[^a-zA-Z0-9\\s]", "");
	                
	                // Create EmployeeSearchCriteria and set the username in codes
	                EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
	                criteria.setCodes(Collections.singletonList(username)); 

	                RequestInfo requestInfo = createRequestInfo();
	               
	                RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
	                requestInfoWrapper.setRequestInfo(requestInfo);

	                // Call the internal API search function directly
	                EmployeeResponse employeeResponse = employeeService.search(criteria, requestInfoWrapper.getRequestInfo());

	                if (employeeResponse != null && !employeeResponse.getEmployees().isEmpty()) { 
	                	//employee already exists
	                	respValue = 4;
	                	return respValue;
	                } else {
	                	if(mobno.isEmpty()) {
	                		respValue = 2; 
	                		return respValue;
	                	}
	                    
	                    RequestInfo requestMdmsInfo = createMdmsRequestInfo();
	                    
	                    MdmsResponse response = mdmsService.fetchMDMSDataTenant(requestMdmsInfo, msevaSsoConstants.FETCH_MDMSDATA_TENTANTID);
	                    String mdmsResp = response.toString();
	                    String formattedJson = mdmsResp.substring("MdmsResponse(".length(), mdmsResp.length() - 1);
	                    mdmsResp = "{" + formattedJson + "}";
	                    mdmsResp = mdmsResp.replace("=", ":");
	                 
	                    Map<String, List<String>> distTenantMap = createTenantMapper(mdmsResp);
	                    
	                    
	                    List<String> cityCodes = new ArrayList<>();
	                    
	                    if (distTenantMap.containsKey(distname)) {
	                        cityCodes = distTenantMap.get(distname);
	                        if(cityCodes.isEmpty()) {
	                        	respValue = 3; 
	                        	return respValue;
	                        }
	                    } else {
	                        respValue = 3;
	                        return respValue;
	                    }
	                    
	                    requestInfo.setAuthToken(null);
	                    org.egov.common.contract.request.User contractUser = new org.egov.common.contract.request.User();
	   
	                    contractUser = setContractUserDetails(fullname, username, mobno, email, cityCodes);
	                    requestInfo.setUserInfo(contractUser);
	                    
	                    EmployeeRequest employeeRequest = new EmployeeRequest();
	                    employeeRequest.setRequestInfo(requestInfo);
	                    Employee newEmployee = new Employee();
	                    newEmployee.setCode(username);
	                    newEmployee.setDateOfAppointment(msevaSsoConstants.MSEVA_DOA);
	                    newEmployee.setEmployeeStatus(msevaSsoConstants.EMPLOYEE_EMPSTATUS);
	                    newEmployee.setEmployeeType(msevaSsoConstants.EMPLOYEE_EMPTYPE);
	                    List<Assignment> assignments = setAssignmentDetails();
	                    newEmployee.setAssignments(assignments);
	                    newEmployee.setTenantId(cityCodes.get(0));
	                    
	                    org.egov.hrms.web.contract.User webUser = new org.egov.hrms.web.contract.User();
	                    webUser = setUserDetails(username,mobno,cityCodes);
	                    newEmployee.setUser(webUser);
	                    List<Jurisdiction> jurisdictions = setJurisdictionsDetails(cityCodes);
	                    newEmployee.setJurisdictions(jurisdictions);
	                    
	                    employeeRequest.setEmployees(Collections.singletonList(newEmployee));
	                    
	                    EmployeeResponse employeeSaveResponse = employeeService.create(employeeRequest); 
                    	if(employeeSaveResponse != null) {
                    		respValue = 5; 
                    	}
	                }
	            }
	        }
	    } catch (Exception e) {
	    	log.error("An error occurred: ", e);
	    }

	    return respValue;
	}
			
	private RequestInfo createMdmsRequestInfo() {
		RequestInfo requestMdmsInfo = new RequestInfo();
		requestMdmsInfo.setApiId(msevaSsoConstants.REQUESTINFO_API_ID);
        requestMdmsInfo.setVer(msevaSsoConstants.REQUESTINFO_API_VER);
        requestMdmsInfo.setAction(msevaSsoConstants.REQUESTINFO_API_MDMSACTION);
        requestMdmsInfo.setDid(msevaSsoConstants.REQUESTINFO_API_DID);
        requestMdmsInfo.setKey(msevaSsoConstants.REQUESTINFO_API_KEY);
        requestMdmsInfo.setMsgId(msevaSsoConstants.REQUESTINFO_API_MSGID);
        requestMdmsInfo.setAuthToken(null);
        return requestMdmsInfo;
	}

	private RequestInfo createRequestInfo() {
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setApiId(msevaSsoConstants.REQUESTINFO_API_ID);
        requestInfo.setVer(msevaSsoConstants.REQUESTINFO_API_VER);
        requestInfo.setAction(msevaSsoConstants.REQUESTINFO_API_ACTION);
        requestInfo.setDid(msevaSsoConstants.REQUESTINFO_API_DID);
        requestInfo.setKey(msevaSsoConstants.REQUESTINFO_API_KEY);
        requestInfo.setMsgId(msevaSsoConstants.REQUESTINFO_API_MSGID);
        //requestInfo.setRequesterId("");
        requestInfo.setAuthToken(msevaSsoConstants.REQUESTINFO_API_AUTHTOKEN);
        return requestInfo;
	}

	private User setContractUserDetails(String fullname, String name, String mobno, String email, List<String> cityCodes) {
		User user = new User();
		user.setId(msevaSsoConstants.USER_INFO_ID);
		user.setName(fullname);
		UUID uuid = UUID.randomUUID();
		System.out.println("UUID : "+uuid.toString());		
		user.setUuid(uuid.toString());
		user.setUserName(name);
		user.setEmailId(email);
		user.setMobileNumber(mobno);
		user.setType(msevaSsoConstants.USER_INFO_EMPTYPE);
		user.setTenantId(cityCodes.get(0));
		List<org.egov.common.contract.request.Role> roles = setRoleswithTenantsForUserInfo(cityCodes);
		user.setRoles(roles);
		return user;
	}

	private List<Jurisdiction> setJurisdictionsDetails(List<String> cityCodes) {
	    List<Jurisdiction> jurisdictions = new ArrayList<>();
	
	    for (String cityCode : cityCodes) {
	        Jurisdiction jurisdiction = new Jurisdiction();
	        jurisdiction.setHierarchy(msevaSsoConstants.JURISDICTION_HIERARCHY);
	        jurisdiction.setBoundaryType(msevaSsoConstants.JURISDICTION_BOUNDARYTYPE);
	        jurisdiction.setBoundary(cityCode);
	        jurisdiction.setTenantId(cityCode);

	        List<JurisdictionRole> roles = new ArrayList<>();
	        StringBuilder furnishedRolesList = new StringBuilder();

	        for (Map.Entry<String, String> entry : msevaSsoConstants.codesNames.entrySet()) {
	            JurisdictionRole role = new JurisdictionRole();
	            role.setValue(entry.getKey());
	            role.setLabel(entry.getValue());
	            roles.add(role);

	            furnishedRolesList.append(entry.getValue()).append(", ");
	        }

	      
	        if (furnishedRolesList.length() > 0) {
	            furnishedRolesList.setLength(furnishedRolesList.length() - 2); 
	        }

	        jurisdiction.setRoles(roles);
	        jurisdiction.setFurnishedRolesList(furnishedRolesList.toString());
	        jurisdictions.add(jurisdiction);
	    }

	    return jurisdictions;
	}

		private org.egov.hrms.web.contract.User setUserDetails(String name, String mobno, List<String> cityCodes) {
			org.egov.hrms.web.contract.User user = new org.egov.hrms.web.contract.User();
			user.setName(name);
			user.setMobileNumber(mobno);
			user.setRelationship(GuardianRelation.Father);
			user.setGender(msevaSsoConstants.EMPLOYEE_SETGENDER);
			user.setDob(msevaSsoConstants.MSEVA_DOB);
			List<Role> roles = setRoleswithTenants(cityCodes);
			user.setTenantId(cityCodes.get(0));			
			user.setRoles(roles);
			user.setPassword(null);
			return user;
	    }
		
		private List<org.egov.common.contract.request.Role> setRoleswithTenantsForUserInfo(List<String> cityCodes) {
			List<org.egov.common.contract.request.Role> roles = new ArrayList<>();
			
		    for (String city : cityCodes) {
		        for (Map.Entry<String, String> entry : msevaSsoConstants.userInfoCodesNames.entrySet()) {
		          
		        	org.egov.common.contract.request.Role role = new org.egov.common.contract.request.Role();
		            role.setCode(entry.getKey());       
		            role.setName(entry.getValue());    
		            role.setTenantId(city);            
		            
		            roles.add(role);
		        }
		    }
			return roles;
		}
		
		private List<Role> setRoleswithTenants(List<String> cityCodes) {
			List<Role> roles = new ArrayList<>();
		    for (String city : cityCodes) {
		        for (Map.Entry<String, String> entry : msevaSsoConstants.codesNames.entrySet()) {
		            
		            Role role = new Role();
		            role.setCode(entry.getKey());       
		            role.setName(entry.getValue());    
		            role.setTenantId(city);            
		            
		            roles.add(role);
		        }
		    }
			return roles;
		}


		private List<Assignment> setAssignmentDetails() {
			List<Assignment> assignments = new ArrayList<>();
			Assignment assign = new Assignment();
			assign.setFromDate(msevaSsoConstants.MSEVA_DOA);
			assign.setToDate(null);
			assign.setIsCurrentAssignment(msevaSsoConstants.IS_CURRENT_ASSIGNMENT);
			assign.setDepartment(msevaSsoConstants.ASSIGNEMENT_DEPARTMENT);
			assign.setDesignation(msevaSsoConstants.ASSIGNEMENT_DESIGNATION);
			assignments.add(assign);
			return assignments;
		}

		private Map<String, List<String>> createTenantMapper(String jsonResponse ) {
			Map<String, List<String>> districtCityMap = new HashMap<>();
			
	        JSONObject jsonObject = new JSONObject(jsonResponse);
	        JSONObject mdmsRes = jsonObject.getJSONObject(msevaSsoConstants.MDMS_RESP);
	        JSONObject tenant = mdmsRes.getJSONObject(msevaSsoConstants.MDMS_RESP_TENANT);
	        JSONArray tenants = tenant.getJSONArray(msevaSsoConstants.MDMS_RESP_TENANTS);
	        
	        
	        for (int i = 0; i < tenants.length(); i++) {
	            JSONObject tenantObj = tenants.getJSONObject(i);
	            String cityCode = tenantObj.getString(msevaSsoConstants.MDMS_RESP_CODE);
	            JSONObject city = tenantObj.getJSONObject(msevaSsoConstants.MDMS_RESP_CITY);
	            String districtName = city.getString(msevaSsoConstants.MDMS_RESP_DISTNAME);

	            
	            districtCityMap.computeIfAbsent(districtName, k -> new ArrayList<>()).add(cityCode);
	        }
			return districtCityMap;
	    }

	public String generateSsoUrl(AuthenticateUserInputRequest authenticateUserInputRequest) { 
		byte[] staticKey =Base64.getDecoder().decode(propertiesManager.hrmsMsevaSsoKey);

		final byte[] StaticIv = new byte[16];
		
		String inputData = authenticateUserInputRequest.getTokenName()+":"+authenticateUserInputRequest.getUserName();
		String encdata = encryptData(inputData,staticKey,StaticIv);	
		
		
		int rd = ReadValuesFromApi(encdata);
        String message = null;
        if(rd == 0) {
        	message = msevaSsoConstants.AUTHFAIL_RESPSTATUS;
        }
        else if(rd == 2) {
        	message = msevaSsoConstants.SAVEFAIL_MOB_RESPSTATUS;
        }
        else if(rd == 3) {
        	message = msevaSsoConstants.SAVEFAIL_DIST_RESPSTATUS;
        }
        else if(rd == 4) {
        	log.info(msevaSsoConstants.EMPEXISTS_RESPSTATUS);
        	message = propertiesManager.hrmsMsevaUatApiEndPoint;
        }
        else if(rd == 5) {
        	log.info(msevaSsoConstants.EMPSAVE_SUCCESS_RESPSTATUS);
        	message = propertiesManager.hrmsMsevaUatApiEndPoint;
        }
        else {
               message = msevaSsoConstants.SAVEFAIL_RESPSTATUS;
        }
        return message;	
	}
	
	
	public static String encryptData(String plainText, byte[] key, byte[] iv) {
        try {
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encryptedBytes = cipher.doFinal(plainBytes);

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
        	log.error("An error occurred: ", e);
            return null; 
        }
    }

	public Map<String, Object> fetchGenerateSsoUrlDetails(AuthenticateUserInputRequest authenticateUserInputRequest) {
		byte[] staticKey =Base64.getDecoder().decode(propertiesManager.hrmsMsevaSsoKey);
		final byte[] StaticIv = new byte[16];
		String inputData = authenticateUserInputRequest.getTokenName()+":"+authenticateUserInputRequest.getUserName();
		String encdata = encryptData(inputData,staticKey,StaticIv);	
		Map<String, Object> map1= ReadValuesFromApi2(encdata);
		return map1;
	}
	
	private Map<String, Object> ReadValuesFromApi2(String decpData) {
	    int respValue = 0;

		Map<String, Object> finalResults =new  HashMap<String,Object>();

	    StringBuilder uri = new StringBuilder();
	    uri.append(propertiesManager.hrmsEsevaApiHost).append(propertiesManager.hrmsEsevaApiEndPoint); // Construct URL for HRMS Eseva call

	    Map<String, Object> apiRequest = new HashMap<>();
	    apiRequest.put(propertiesManager.hrmsEsevaApiRequestObject, decpData); //hrmsEsevaApiRequestObject

	    try {
	        Map<String, Object> responseMap = (Map<String, Object>) restCallRepository.fetchResult(uri, apiRequest);
			// Map<String, Object> data = (Map<String, Object>) ((List<?>) responseMap.get("data")).get(0);

	        if(responseMap != null) {
	        	Object responseObj = responseMap.get(propertiesManager.hrmsEsevaApiResponseObjectResp); //hrmsEsevaApiResponseObjectResp
	        	
				respValue = Integer.parseInt(responseObj.toString());
	        	if (respValue == 0) {
					finalResults.put("message", msevaSsoConstants.AUTHFAIL_RESPSTATUS);
					finalResults.put("code", 0);
                    return finalResults;
                }
	        }


	        if (responseMap != null && responseMap.containsKey(propertiesManager.hrmsEsevaApiResponseObjectData)) {
	            List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseMap.get(propertiesManager.hrmsEsevaApiResponseObjectData);
	            
				if (!dataList.isEmpty()) {
	                Map<String, Object> dataObject = dataList.get(0);
	                String responseVal = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectResp);
	                respValue = Integer.parseInt(responseVal);
	                String username = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectUsername);
	                username = username.replaceAll("[^a-zA-Z0-9\\s]", "");
	                String mobno = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectMobile);
	                String distname = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectDistrict);
	                String email = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectEmail);
	                String fullname = (String) dataObject.get(propertiesManager.hrmsEsevaApiResponseObjectFullname);
	                fullname = fullname.replaceAll("[^a-zA-Z0-9\\s]", "");
	                
	                // Create EmployeeSearchCriteria and set the username in codes
	                EmployeeSearchCriteria criteria = new EmployeeSearchCriteria();
	                criteria.setCodes(Collections.singletonList(username)); 

	                RequestInfo requestInfo = createRequestInfo();
	               
	                RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
	                requestInfoWrapper.setRequestInfo(requestInfo);

	                // Call the internal API search function directly
	                EmployeeResponse employeeResponse = employeeService.search(criteria, requestInfoWrapper.getRequestInfo());
					List<Employee> employees = employeeResponse.getEmployees();

	                if (employeeResponse != null && !employeeResponse.getEmployees().isEmpty() && employees != null && !employees.isEmpty() ) { 
					
							// Fetch the first employee							
							for (Employee employee : employees) {
								finalResults.put("userName", employee.getCode());
								finalResults.put("tenantId", employee.getTenantId());
								finalResults.put("employeeType", "EMPLOYEE");
								finalResults.put("message", msevaSsoConstants.EMPEXISTS_RESPSTATUS);
								finalResults.put("url", propertiesManager.hrmsMsevaUatApiEndPoint);
							}
					
	                	//employee already exists
	                	respValue = 4;
						finalResults.put("code", 4);
						
	                	return finalResults;
	                } else {
	                	if(mobno.isEmpty()) {
	                		respValue = 2; 
							finalResults.put("message", msevaSsoConstants.SAVEFAIL_MOB_RESPSTATUS);
							finalResults.put("code", 2);
	                		return finalResults;
	                	}
	                    
	                    RequestInfo requestMdmsInfo = createMdmsRequestInfo();
	                    
	                    MdmsResponse response = mdmsService.fetchMDMSDataTenant(requestMdmsInfo, msevaSsoConstants.FETCH_MDMSDATA_TENTANTID);

						String mdmsResp = response.toString();
	                    String formattedJson = mdmsResp.substring("MdmsResponse(".length(), mdmsResp.length() - 1);
	                    mdmsResp = "{" + formattedJson + "}";
	                    mdmsResp = mdmsResp.replace("=", ":");
	                 
	                    Map<String, List<String>> distTenantMap = createTenantMapper(mdmsResp);
	                    
	                    
	                    List<String> cityCodes = new ArrayList<>();
	                    
	                    if (distTenantMap.containsKey(distname)) {
	                        cityCodes = distTenantMap.get(distname);
	                        if(cityCodes.isEmpty()) {
	                        	respValue = 3; 
								finalResults.put("message", msevaSsoConstants.SAVEFAIL_DIST_RESPSTATUS);
								finalResults.put("code", 3);
								return finalResults;
	                        }
	                    } else {
	                        respValue = 3;
							finalResults.put("message", msevaSsoConstants.SAVEFAIL_DIST_RESPSTATUS);
							finalResults.put("code", 3);
							return finalResults;
	                    }
	                    
	                    requestInfo.setAuthToken(null);
	                    org.egov.common.contract.request.User contractUser = new org.egov.common.contract.request.User();
	   
	                    contractUser = setContractUserDetails(fullname, username, mobno, email, cityCodes);
	                    requestInfo.setUserInfo(contractUser);
	                    
	                    EmployeeRequest employeeRequest = new EmployeeRequest();
	                    employeeRequest.setRequestInfo(requestInfo);
	                    Employee newEmployee = new Employee();
	                    newEmployee.setCode(username);
	                    newEmployee.setDateOfAppointment(msevaSsoConstants.MSEVA_DOA);
	                    newEmployee.setEmployeeStatus(msevaSsoConstants.EMPLOYEE_EMPSTATUS);
	                    newEmployee.setEmployeeType(msevaSsoConstants.EMPLOYEE_EMPTYPE);
	                    List<Assignment> assignments = setAssignmentDetails();
	                    newEmployee.setAssignments(assignments);
	                    newEmployee.setTenantId(cityCodes.get(0));
	                    
	                    org.egov.hrms.web.contract.User webUser = new org.egov.hrms.web.contract.User();
	                    webUser = setUserDetails(username,mobno,cityCodes);
	                    newEmployee.setUser(webUser);
	                    List<Jurisdiction> jurisdictions = setJurisdictionsDetails(cityCodes);
	                    newEmployee.setJurisdictions(jurisdictions);
	                    
	                    employeeRequest.setEmployees(Collections.singletonList(newEmployee));
	                    
	                    EmployeeResponse employeeSaveResponse = employeeService.create(employeeRequest); 
                    	if(employeeSaveResponse != null) {
                    		respValue = 5; 				
							List<Employee> createdEmployee = employeeSaveResponse.getEmployees();
							System.out.println(employeeResponse.getEmployees());
					
							Employee employeeObj = createdEmployee.get(0);
							
							finalResults.put("userName", employeeObj.getUser().getName());
							finalResults.put("tenantId", employeeObj.getUser().getTenantId());							
							finalResults.put("employeeType", employeeObj.getUser().getType());
							finalResults.put("message", msevaSsoConstants.EMPSAVE_SUCCESS_RESPSTATUS);
							finalResults.put("url", propertiesManager.hrmsMsevaUatApiEndPoint);		
							finalResults.put("code", 5);
							
                    	}
	                }
	            }
	        }
	    } catch (Exception e) {
	    	log.error("An error occurred: ", e);
		
	    }

	    return finalResults;
	}

}







     
 

             

      

    