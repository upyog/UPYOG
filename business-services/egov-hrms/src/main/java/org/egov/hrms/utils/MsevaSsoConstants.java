package org.egov.hrms.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MsevaSsoConstants {
	
	//ESEVA API REQUEST RESPONSE BODY
	/*public final String ESEVA_REQUEST_OBJECT = "Data"; 
	public final String ESEVA_RESPONSE_RESP = "response";
	public final String ESEVA_RESPONSE_DATA = "data";
	public final String ESEVA_RESPONSE_UN = "user_name";
	public final String ESEVA_RESPONSE_MOB = "mobile_no";
	public final String ESEVA_RESPONSE_EMAIL = "email";
	public final String ESEVA_RESPONSE_DIST = "district_name";
	public final String ESEVA_RESPONSE_FN = "fullname";*/
	
	//MDMS API RESPONSE BODY
	public final String MDMS_RESP = "mdmsRes"; 
	public final String MDMS_RESP_TENANT = "tenant";
	public final String MDMS_RESP_TENANTS = "tenants";
	public final String MDMS_RESP_CODE = "code";
	public final String MDMS_RESP_CITY = "city";
	public final String MDMS_RESP_DISTNAME = "districtName";
	
	//MDMS API FETCHTENANTS
	public final String MDMS_FETCH_TENANTS_REQBODY = "tenants"; 
	public final String MDMS_FETCH_TENANT_REQUESTBODY = "tenant";
	
	//REQUESTINFO constants
	public final String REQUESTINFO_API_ID = "Rainmaker";
	public final String REQUESTINFO_API_VER = ".01";
	public final String REQUESTINFO_API_ACTION = "";
	public final String REQUESTINFO_API_DID = "1";
	public final String REQUESTINFO_API_KEY = "";
	public final String REQUESTINFO_API_MSGID = "20170310130900|en_IN";
	public final String REQUESTINFO_API_AUTHTOKEN = "b5a656aa-ecd4-4bf0-9225-2d759294f87f";
	public final String REQUESTINFO_API_MDMSACTION = "_search";
	
	//MSEVA SSO constants
	public final Long MSEVA_DOA = 1731349800000L; //Date of appointment
	public final Long MSEVA_DOB = 631152000000L; //Dob
	
	//ASSIGNMENT constants
	public final String ASSIGNEMENT_DEPARTMENT = "DEPT_41"; 
	public final String ASSIGNEMENT_DESIGNATION = "DESIG_35";
	public final boolean IS_CURRENT_ASSIGNMENT = true;
	
	//JURISDICTION constants
	public final String JURISDICTION_HIERARCHY = "REVENUE"; 
	public final String JURISDICTION_BOUNDARYTYPE = "City"; 
	
	//USERINFO constants
	public final String USER_INFO_EMPTYPE = "EMPLOYEE";
	public final Long USER_INFO_ID = 10981L;
	
	//FETCH MDMS_DATA constants
	public final String FETCH_MDMSDATA_TENTANTID = "pb";
	
	//EMPLOYEE constants
	public final String EMPLOYEE_EMPSTATUS = "EMPLOYEED";
	public final String EMPLOYEE_EMPTYPE = "DAILYWAGES";
	public final String EMPLOYEE_SETGENDER = "MALE"; //user inside employee
	
	//SSO Response
	public final String AUTHFAIL_RESPSTATUS = "Authentication Failed. Employee not found in Eseva";
	public final String SAVEFAIL_MOB_RESPSTATUS = "Please provide the Mobile number to save in Mseva";
	public final String SAVEFAIL_DIST_RESPSTATUS = "Please provide the valid District name to save in Mseva";
	public final String EMPEXISTS_RESPSTATUS = "Employee already exists in Mseva";
	public final String EMPSAVE_SUCCESS_RESPSTATUS = "Employee newly created in Mseva!";
	public final String SAVEFAIL_RESPSTATUS = "Employee Validated. Authentication Success!. But Employee not Created in Mseva";
	
	
	//userInfo Roles constants in RequestInfo
	public final Map<String, String> userInfoCodesNames;
    // Instance initializer block for userInfoCodesNames used inside UserInfo roles
    {
    	userInfoCodesNames = new HashMap<>();
    	userInfoCodesNames.put("HRMS_ADMIN", "HRMS ADMIN");
    	userInfoCodesNames.put("ULBADMIN", "ULB Administrator");
    	userInfoCodesNames.put("PTULBADMIN", "PT ULB Administrator");
    }
    
    //user Roles constants in Employees
    public final Map<String, String> codesNames;
    // Instance initializer block for codesNames used inside User roles under Employees
    {
    	codesNames = new HashMap<>();
    	codesNames.put("WS_CLERK", "WS Clerk");
    	codesNames.put("NOC_CEMP", "NoC counter employee");
    	codesNames.put("TL_CEMP", "TL Counter Employee");
		codesNames.put("SW_CLERK", "SW Clerk");
		codesNames.put("CEMP", "Counter Employee");
		codesNames.put("WS_CEMP", "WS Counter Employee");
		codesNames.put("PTCEMP", "PT Counter Employee");
		codesNames.put("ESEWAEMP", "Esewa Employee");
    }
    
    
    
}
