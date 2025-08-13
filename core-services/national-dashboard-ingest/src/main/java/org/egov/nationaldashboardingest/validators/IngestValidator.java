package org.egov.nationaldashboardingest.validators;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.repository.ElasticSearchRepository;
import org.egov.nationaldashboardingest.repository.IngestDataRepository;
import org.egov.nationaldashboardingest.utils.IngestConstants;
import org.egov.nationaldashboardingest.utils.JsonProcessorUtil;
import org.egov.nationaldashboardingest.web.models.AckEntity;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestAckData;
import org.egov.nationaldashboardingest.web.models.IngestRequest;
import org.egov.nationaldashboardingest.web.models.MasterData;
import org.egov.nationaldashboardingest.web.models.MasterDataRequest;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IngestValidator {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JsonProcessorUtil jsonProcessorUtil;

    @Autowired
    private ElasticSearchRepository repository;

    @Autowired
    private IngestDataRepository dataRepository;

    @Autowired
    private Producer producer;
    
    @Value("${egov.mdms.host}")
	private String mdmsHost;
	
	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;
	
	@Autowired
	private RestTemplate restTemplate;

    private static final Pattern p = Pattern.compile("[^a-z0-9._()/&:,\\- ]", Pattern.CASE_INSENSITIVE);
    
	public static final String MDMS_NATIONALTENANTS_PATH = "$.MdmsRes.tenant.nationalInfo";

	public static final String MDMS_PROPERTYTYPE_PATH = "$.MdmsRes.tenant.propertyType";
	
	public static final String MDMS_PAYMENTCHANNEL_PATH = "$.MdmsRes.common-masters.PaymentChannel";

    public static final String MDMS_NATIONALTENANTUSER_PATH = "$.MdmsRes.tenant.nationalInfoUser";

//    public void verifyCrossStateRequest(Data data, RequestInfo requestInfo){
//        String employeeUlb = requestInfo.getUserInfo().getTenantId();
//        Set<String> roles = new HashSet<>();
//        requestInfo.getUserInfo().getRoles().forEach(role -> {
//            roles.add(role.getCode());
//        });
//
//        // Skip validations in case the user is having adaptor ingest specific role
//        if(!roles.contains(applicationProperties.getAdaptorIngestSystemRole())) {
//            if (roles.contains("SUPERUSER")) {
//                String ulbPresentInRequest = data.getUlb();
//                log.info(ulbPresentInRequest.split("\\.")[0]);
//                if (!ulbPresentInRequest.split("\\.")[0].equals(employeeUlb.split("\\.")[0])) {
//                    throw new CustomException("EG_INGEST_ERR", "Superusers of one state cannot insert data for another state");
//                }
//
//            } else {
//                String ulbPresentInRequest = data.getUlb();
//                if (ulbPresentInRequest.contains(".")) {
//                    if (!employeeUlb.equals(ulbPresentInRequest))
//                        throw new CustomException("EG_INGEST_ERR", "Employee of ulb: " + employeeUlb + " cannot insert data for ulb: " + ulbPresentInRequest);
//                } else {
//                    if (!employeeUlb.contains(ulbPresentInRequest.toLowerCase()))
//                        throw new CustomException("EG_INGEST_ERR", "Employee of ulb: " + employeeUlb + " cannot insert data for ulb: " + ulbPresentInRequest);
//                }
//            }
//        }
//    }
	
	public void verifyCrossStateRequest(Data data, RequestInfo requestInfo){
        String uuid = requestInfo.getUserInfo().getUuid();
  
        Map<String,String> userUUID=applicationProperties.getNationalDashboardUser();
        if(!userUUID.get("SUPERUUID").equalsIgnoreCase(uuid) && !userUUID.get(data.getState()).contains(uuid)) {
                 throw new CustomException("EG_CROSS_STATE_DATA_INGEST", "Employee of one state cannot insert data of another State!!");
               
        }
    }

    public void verifyCrossStateMasterDataRequest(MasterDataRequest masterDataRequest) {
        String employeeUlb = masterDataRequest.getRequestInfo().getUserInfo().getTenantId();
        String ulbPresentInRequest = masterDataRequest.getMasterData().getUlb();
        Set<String> roles = new HashSet<>();
        masterDataRequest.getRequestInfo().getUserInfo().getRoles().forEach(role -> {
            roles.add(role.getCode());
        });
        // Skip validations in case the user is having adaptor ingest specific role
        if(!roles.contains(applicationProperties.getAdaptorIngestSystemRole())) {
            if (ulbPresentInRequest.contains(".")) {
                if (!employeeUlb.equals(ulbPresentInRequest))
                    throw new CustomException("EG_MASTER_DATA_INGEST_ERR", "Employee of ulb: " + employeeUlb + " cannot insert data for ulb: " + ulbPresentInRequest);
            } else {
                if (!employeeUlb.contains(ulbPresentInRequest.toLowerCase()))
                    throw new CustomException("EG_MASTER_DATA_INGEST_ERR", "Employee of ulb: " + employeeUlb + " cannot insert data for ulb: " + ulbPresentInRequest);
            }
        }
    }

    public void verifyDataStructure(Data ingestData){

        /*What happens: The method first checks basic fields like:
        Date: Makes sure the date is in the correct format
        ULB, Ward, Region, State: Makes sure these text fields don't contain numbers where they shouldn't 
        */
        validateDateFormat(ingestData.getDate());
        validateStringNotNumeric(ingestData.getUlb());
        validateStringNotNumeric(ingestData.getWard());
        validateStringNotNumeric(ingestData.getRegion());
        validateStringNotNumeric(ingestData.getState());


        /*What happens:
            Removes colons (:) from ward names and replaces them with spaces
            Converts state names to camelCase format (like "new york" becomes "newYork") 
        */
        if(ingestData.getWard().contains(":"))
        	ingestData.setWard(ingestData.getWard().replace(":"," "));
		
        ingestData.setState(toCamelCase(ingestData.getState()));


        /*What happens:
        Looks up what fields are allowed for this specific module
        Think of it like checking a rulebook: "For module X, these are the only fields allowed"
        If no rules exist for this module, it throws an error*/
        Set<String> configuredFieldsForModule = new HashSet<>();
        if(applicationProperties.getModuleFieldsMapping().containsKey(ingestData.getModule()))
            configuredFieldsForModule = applicationProperties.getModuleFieldsMapping().get(ingestData.getModule()).keySet();
        else
            throw new CustomException("EG_DS_VALIDATE_ERR", "Field mapping has not been configured for module code: " + ingestData.getModule());

        try {
            Map<String, JsonNodeType> keyVsTypeMap = new HashMap<>();
            /**
             * Converts the data to JSON format for easier analysis
                Extracts the "metrics" section from the data
                Gets a list of all field names in the metrics
             */
            String seedData = objectMapper.writeValueAsString(ingestData);
            //get all the data in the json formats
            JsonNode incomingData = objectMapper.readValue(seedData, JsonNode.class);
            List<String> keyNames = new ArrayList<>();
            //extarcting the metrics which we send inside teh Payload and data
            JsonNode metricsData = incomingData.get(IngestConstants.METRICS);
            jsonProcessorUtil.enrichKeyNamesInList(metricsData, keyNames);

            /*
             * For each field in your data, it checks: "Is this field allowed for this module?"
                If any field is not in the allowed list, it throws an error
                It also records what type each field is (number, text, array, etc.)
             */
            for(String inputKeyName : keyNames){
                keyVsTypeMap.put(inputKeyName, metricsData.get(inputKeyName).getNodeType());
                // if any of key which is inisde metrics in your payload is not contained then it will thorwi thsi error 
                if(!configuredFieldsForModule.contains(inputKeyName))
                    throw new CustomException("EG_DS_VALIDATE_ERR", "The metric: " + inputKeyName + " was not configured in field mapping for module: " + ingestData.getModule());
            }

            /*This is the most complex part. For each field, it checks two scenarios:
             * 1.  Array Fields (Complex Data)
             * 2.  Simple Fields
            */

            /*keyVsTypeMap contains all field names from your metrics and their detected data types or your data, this would include keys like: "todaysApplications", "transactions", "todaysCollection", etc. */
            keyVsTypeMap.keySet().forEach(key ->{
                JsonNodeType type = keyVsTypeMap.get(key);
                //extracting all the keys from module fields mapping and checking if there is any array type for modules
                /*
                 * The :: acts as a delimiter where:
                    Left side ("usageCategory"): Describes what the array represents
                    Right side ("NUMBER"): The expected data type of values in the array

                    Field: "todaysCollection"
                    Configuration: "usageCategory::NUMBER"
                    Result: Contains "::" → This is an array field
                 */
                if(applicationProperties.getModuleFieldsMapping().get(ingestData.getModule()).get(key).contains("::")){
                    /*
                     * Configuration: "usageCategory::NUMBER"
                        After split("::")[1]: Gets "NUMBER"
                        valueType: "NUMBER"
                     */
                    String valueType = applicationProperties.getModuleFieldsMapping().get(ingestData.getModule()).get(key).split("::")[1];
                    //if any key is added as a array but you send data in other format it throw this error 
                    if(!(metricsData.get(key) instanceof ArrayNode)){
                        throw new CustomException("EG_DS_VALIDATE_ERR", "Key: " + key + " is configured as type array but received value of type: " + type.toString());
                    }else{
                        for(JsonNode childNode : metricsData.get(key)){
                            // Validate groupBy field names for consistency
                            String inputGroupByField = childNode.get("groupBy").asText(); // get the name of group by from metrics
                            if(!applicationProperties.getModuleAllowedGroupByFieldsMapping().containsKey(ingestData.getModule()))
                                throw new CustomException("EG_DS_VALIDATE_ERR", "Allowed groupBy fields mapping are mandatory for array type fields. It has not been configured for module: " + ingestData.getModule());
                            else
                            if(!applicationProperties.getModuleAllowedGroupByFieldsMapping().get(ingestData.getModule()).contains(inputGroupByField))
                                throw new CustomException("EG_DS_VALIDATE_ERR", "Group by field provided in input: " + inputGroupByField + " is not configured for module: " + ingestData.getModule() + ". Please note that the field name provided against groupBy metric in ingest payload should exactly match the field name provided in allowed fields configuration.");
                            // Validate data type of values passed in ingest API
                            for(JsonNode bucketNode : childNode.get("buckets")) {
                                if (!(bucketNode.get("value").getNodeType().toString().equalsIgnoreCase(valueType)))
                                    throw new CustomException("EG_DS_VALIDATE_ERR", "Children values of the array: " + key + " should only contain values of type: " + valueType);
                            }
                        }
                    }
                } else {
                    if (!type.toString().equalsIgnoreCase(applicationProperties.getModuleFieldsMapping().get(ingestData.getModule()).get(key)))
                        throw new CustomException("EG_DS_VALIDATE_ERR", "The type of data input does not match with the type of data provided in configuration for key: " + key);
                }
            });

        }catch (JsonProcessingException e){
            throw new CustomException("EG_PAYLOAD_READ_ERR", "Error occured while processing ingest data");
        }

    }

    private void validateStringNotNumeric(String s) {
        if(s.length() == 1)
            if(s.equals("-") || s.equals("."))
                throw new CustomException("EG_DS_ERR", "Cannot have a string of length 1 containing separator( . OR - ) as ingest input.");

        Boolean hasNewLine = s.contains("\n");
        StringBuilder temp = new StringBuilder(s);
        if(hasNewLine){
            s = s.replaceAll("\n", "");
        }
        Matcher m = p.matcher(s);
        s = temp.toString();
        if (m.find())
            throw new CustomException("EG_DS_ERR", "Special characters are not allowed in input.");

        if (NumberUtils.isParsable(s)) {
            throw new CustomException("EG_DS_ERR", "Received numeric value: " + s + ". Please provide String value strictly.");
        }
    }

    private void validateDateFormat(String date) {
        if (!isValidSystemDefinedDateFormat(date))
            throw new CustomException("EG_DS_ERR", "Date should be strictly in dd-MM-yyyy format.");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);
        Date currDate = new Date();
        try {
            Date inpDate = formatter.parse(date);
            if(inpDate.after(currDate))
                throw new CustomException("EG_DS_ERR", "Future date values are not allowed.");
        } catch (ParseException e) {
            throw new CustomException("EG_DS_ERR", "Date should be strictly in dd-MM-yyyy format.");
        }
    }

    private boolean isValidSystemDefinedDateFormat(String date) {
        if(!date.contains("-"))
            return false;

        String []dateArr = date.split("-");
        if(dateArr.length != 3){
            return false;
        }
        if(dateArr[0].length() == 2 && dateArr[1].length() == 2 && dateArr[2].length() == 4)
            return true;
        return false;
    }

    public void verifyMasterDataStructure(MasterData masterData) {
        validateStringNotNumeric(masterData.getModule());
        validateStringNotNumeric(masterData.getRegion());
        validateStringNotNumeric(masterData.getState());
        validateStringNotNumeric(masterData.getUlb());
        validateFinancialYear(masterData.getFinancialYear());
        Set<String> configuredFieldsForModule = new HashSet<>();

        if(applicationProperties.getModuleFieldsMapping().containsKey(masterData.getModule()))
            configuredFieldsForModule = applicationProperties.getMasterModuleFieldsMapping().get(masterData.getModule()).keySet();
        else
            throw new CustomException("EG_DS_VALIDATE_ERR", "Master field mapping has not been configured for module code: " + masterData.getModule());
        try {
            Map<String, JsonNodeType> keyVsTypeMap = new HashMap<>();
            String seedData = objectMapper.writeValueAsString(masterData);
            JsonNode incomingData = objectMapper.readValue(seedData, JsonNode.class);
            List<String> keyNames = new ArrayList<>();
            JsonNode metricsData = incomingData.get(IngestConstants.METRICS);
            jsonProcessorUtil.enrichKeyNamesInList(metricsData, keyNames);

            for(String inputKeyName : keyNames){
                keyVsTypeMap.put(inputKeyName, metricsData.get(inputKeyName).getNodeType());
                if(!configuredFieldsForModule.contains(inputKeyName))
                    throw new CustomException("EG_DS_VALIDATE_ERR", "The metric: " + inputKeyName + " was not configured in master field mapping for module: " + masterData.getModule());
            }

            if(keyNames.size() < configuredFieldsForModule.size()){
                List<String> absentFields = new ArrayList<>();
                configuredFieldsForModule.forEach(field -> {
                    if(!keyNames.contains(field))
                        absentFields.add(field);
                });
                throw new CustomException("EG_DS_VALIDATE_ERR", "Received less number of fields than the number of fields configured in master field mapping for module: " + masterData.getModule() + ". List of absent fields: " + absentFields.toString());
            }

            keyVsTypeMap.keySet().forEach(key ->{
                JsonNodeType type = keyVsTypeMap.get(key);
                if(applicationProperties.getMasterModuleFieldsMapping().get(masterData.getModule()).get(key).contains("::")){
                    String valueType = applicationProperties.getMasterModuleFieldsMapping().get(masterData.getModule()).get(key).split("::")[1];
                    if(!(metricsData.get(key) instanceof ArrayNode)){
                        throw new CustomException("EG_DS_VALIDATE_ERR", "Key: " + key + " is configured as type array but received value of type: " + type.toString());
                    }else{
                        for(JsonNode childNode : metricsData.get(key)){
                            for(JsonNode bucketNode : childNode.get("buckets")) {
                                if (!(bucketNode.get("value").getNodeType().toString().equalsIgnoreCase(valueType)))
                                    throw new CustomException("EG_DS_VALIDATE_ERR", "Children values of the array: " + key + " should only contain values of type: " + valueType);
                            }
                        }
                    }
                } else {
                    if (!type.toString().equalsIgnoreCase(applicationProperties.getMasterModuleFieldsMapping().get(masterData.getModule()).get(key)))
                        throw new CustomException("EG_DS_VALIDATE_ERR", "The type of data input does not match with the type of data provided in configuration for key: " + key);
                }
            });


        }catch (JsonProcessingException e){
            throw new CustomException("EG_PAYLOAD_READ_ERR", "Error occured while processing ingest data");
        }
    }

    private void validateFinancialYear(String financialYear) {
        if(!financialYear.contains("-"))
            throw new CustomException("EG_MASTER_DATA_VALIDATE_ERR", "Financial year is not given in correct format. Correct format is YYYY-YY");

        String fromYear = financialYear.split("-")[0];
        String toYear = financialYear.split("-")[1];
        if((!NumberUtils.isParsable(fromYear) || !NumberUtils.isParsable(toYear))){
            throw new CustomException("EG_MASTER_DATA_VALIDATE_ERR", "Financial year is not given in proper format.");
        }
    }

    public Boolean verifyTenant(RequestInfo requestInfo,List<Data> ingestData) {
    	

	Boolean isTenantValid=false;
	int validCounts=0;
	
	StringBuilder mdmsURL=new StringBuilder().append(mdmsHost).append(mdmsEndpoint);
   
	MasterDetail mstrDetail = MasterDetail.builder().name("nationalInfo")
			.filter("[?(@.active==true)]")
			.build();
	
	
    // contructing a module detail to send in MDMS - tenant is the module name and nationalInfo is the masterName
	ModuleDetail moduleDetail = ModuleDetail.builder().moduleName("tenant").masterDetails(Arrays.asList(mstrDetail)).build();
    //here it is sending module details and tenant id to fetch the data from MDMS
	MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId("pg").build();
	MdmsCriteriaReq mdmsConfig = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
	Object response = null;
	List<Map<String,String>> jsonOutput=null;
	
	log.info("URI: " + mdmsURL.toString());
	try {
			log.info(objectMapper.writeValueAsString(mdmsConfig));
			response = restTemplate.postForObject(mdmsURL.toString(), mdmsConfig, Map.class);
			jsonOutput=  JsonPath.read(response, MDMS_NATIONALTENANTS_PATH);
			//migratedTenant= jsonOutput.get(0);
			
		} catch (ResourceAccessException e) {
			
			Map<String, String> map = new HashMap<>();
			map.put(null, e.getMessage());
			throw new CustomException(map);
		}  catch (HttpClientErrorException e) {

			log.info("the error is : " + e.getResponseBodyAsString());
			throw new ServiceCallException(e.getResponseBodyAsString());
		}catch (Exception e) {

			log.error("Exception while fetching from searcher: ", e);
		}

	for(Data data:ingestData)
	{
        String tenant=data.getUlb();
        data.setState(toCamelCase(data.getState()));
        String state=data.getState();
        
    
       	for(Map<String,String> migratedTenants:jsonOutput)
        	{
        		if(tenant.equals(migratedTenants.get("code")) && state.equals(migratedTenants.get("stateName")))
        		{
        		validCounts++;
        		break;
        		}
        	}
	}
	
	if(validCounts ==ingestData.size())
		isTenantValid=true;
	return isTenantValid;
    }
	 public Boolean verifyUsage(Data ingestData, Set < String > usageList) {

        Set < String > usageCategory = new HashSet < String > ();
        HashMap < String, Object > map = ingestData.getMetrics();
        List < String > keyToFetch = null;
        Boolean isValid=false;
        int validCounts=0;
        
        Boolean isUsageCategoryInvalid = false;

        /* Commenting out because we simplified this approach below
        if (ingestData.getModule() != null && ingestData.getModule().equals("COMMON") || ingestData.getModule().equals("PGR") || ingestData.getModule() != null && ingestData.getModule().equals("TL") || ingestData.getModule() != null && ingestData.getModule().equals("OBPS") ||ingestData.getModule() != null && ingestData.getModule().equals("MCOLLECT") ) {
            keyToFetch = null;
            isUsageCategoryInvalid = true;
        }
        */

        // Check if module is one of the specific modules that require usage category validation
        Set<String> validModules = new HashSet<>(Arrays.asList("COMMON", "PGR", "TL", "OBPS", "MCOLLECT", "SV","FINANCE"));
        String module = ingestData.getModule();
        log.info("Processing usage type for module: {}", module);
        
        if (module != null && validModules.contains(module)) {
            keyToFetch = null;
            isUsageCategoryInvalid = true;
        }
            
        if (ingestData.getModule() != null && ingestData.getModule().equals("PT")) {
            keyToFetch = applicationProperties.getNationalDashboardUsageTypePT();
        }
        else if (ingestData.getModule() != null && ingestData.getModule().equals("WS")) {
            keyToFetch = applicationProperties.getNationalDashboardUsageTypeWS();
        }
        else if (ingestData.getModule() != null && ingestData.getModule().equals("FIRENOC")) {
            keyToFetch = applicationProperties.getNationalDashboardUsageTypeNOC();
        }
        else if (ingestData.getModule() != null && ingestData.getModule().equals("FSM")) {
            keyToFetch = applicationProperties.getNationalDashboardUsageTypeFSM();
        }

        if (keyToFetch != null) {
            for (String key: keyToFetch) {
                List < HashMap < String, Object >> values = (List < HashMap < String, Object >> ) map.get(key);
                if (values!=null) {
                for (HashMap < String, Object > a: values) {
                    if (a.get("groupBy").equals("usageCategory") || a.get("groupBy").equals("usageType")) {
                        List < HashMap < String, String >> valuess = (List < HashMap < String, String >> ) a.get("buckets");
                        for (HashMap < String, String > b: valuess)
                            usageCategory.add(toCamelCase(b.get("name")));
                    }
                }
                }
            }
    		for (String uc: usageCategory) {
    			isValid=usageList.contains(uc); 
    			if(!isValid)
    				break;
    			else
    				validCounts++;
    				
    				
            }
            if(validCounts==usageCategory.size())
        		isUsageCategoryInvalid=true;
        }

    	return isUsageCategoryInvalid;
    }
	
	public Boolean verifyPayment(Data ingestData, Set < String > paymentChannelList) {

	        Set < String > PaymentChannel = new HashSet < String > ();
	        HashMap < String, Object > map = ingestData.getMetrics();
	        List < String > keyToFetch = null;
	        Boolean isValid=false;
	        int validCounts=0;
	        
            /* 
            Commenting out because we simplified this approach below 

	        Boolean isPaymentChannelInvalid = false;
	        if (ingestData.getModule() != null && (ingestData.getModule().equals("COMMON")||ingestData.getModule().equals("PGR")) ) {
	            keyToFetch = null;
	            isPaymentChannelInvalid = true;
	        }
	    
	        if (ingestData.getModule() != null && (ingestData.getModule().equals("PT")|| ingestData.getModule().equals("FIRENOC") || ingestData.getModule().equals("BIRTH") || ingestData.getModule().equals("DEATH") || ingestData.getModule().equals("TL") || ingestData.getModule().equals("FSM") || ingestData.getModule().equals("WS") || ingestData.getModule().equals("OBPS")) ) {
	            keyToFetch = applicationProperties.getNationalDashboardpaymentChannel();
	        }
	        else if (ingestData.getModule() != null && ingestData.getModule().equals("MCOLLECT")) {
	            keyToFetch = applicationProperties.getNationalDashboardpaymentChannelMISC();
	        }
            */


            // Define module groups for payment channel handling
            Set<String> moduleWithoutPayment = new HashSet<>(Arrays.asList("COMMON", "PGR"));
            Set<String> moduleWithPayment = new HashSet<>(Arrays.asList("PT", "FIRENOC", "TL", "FSM", "WS", "OBPS", "SV","FINANCE"));

            String module = ingestData.getModule();
            Boolean isPaymentChannelInvalid = false;

            log.info("Processing payment channel for module: {}", module);

            if (module != null) {
                if (moduleWithoutPayment.contains(module)) {
                    // COMMON and PGR modules have invalid payment channels
                    keyToFetch = null;
                    isPaymentChannelInvalid = true;
                } else if (moduleWithPayment.contains(module)) {
                    // Standard modules use national dashboard payment channel
                    keyToFetch = applicationProperties.getNationalDashboardpaymentChannel();
                } else if (module.equals("MCOLLECT")) {
                    // MCOLLECT uses miscellaneous payment channel
                    keyToFetch = applicationProperties.getNationalDashboardpaymentChannelMISC();
                }
            }

	        if (keyToFetch != null) {
	            for (String key: keyToFetch) {
	                List < HashMap < String, Object >> values = (List < HashMap < String, Object >> ) map.get(key);
	                if (values!=null) {
	                for (HashMap < String, Object > a: values) {
	                    if (a.get("groupBy").equals("paymentChannelType") || a.get("groupBy").equals("paymentMode")) {
	                        List < HashMap < String, String >> valuess = (List < HashMap < String, String >> ) a.get("buckets");
	                        for (HashMap < String, String > b: valuess)
	                        	PaymentChannel.add(toCamelCase(b.get("name")));
	                    }
	                }
	                }
	            }
	    		for (String pc: PaymentChannel) {
	    			isValid=paymentChannelList.contains(pc); 
	    			if(!isValid)
	    				break;
	    			else
	    				validCounts++;
	    				
	    				
	            }
	            if(validCounts==PaymentChannel.size())
	            	isPaymentChannelInvalid=true;
	        }

	    	return isPaymentChannelInvalid;
	    }
    
    public Set < String > verifyPropertyType(RequestInfo requestInfo, List < Data > ingestData) {


        Boolean isUsageTypeValid = false;
        int validCounts = 0;

        //get the mdms host 
        StringBuilder mdmsURL = new StringBuilder().append(mdmsHost).append(mdmsEndpoint);

        // setting master name - propertyType and get only those who is active true only
        MasterDetail mstrDetail = MasterDetail.builder().name("propertyType")
            .filter("[?(@.active==true)]")
            .build();


        // contructing a module detail to send in MDMS - tenant is the module name and propertyType is the masterName
        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName("tenant").masterDetails(Arrays.asList(mstrDetail)).build();
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId("pg").build();
        MdmsCriteriaReq mdmsConfig = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
        Object response = null;
        List < Map < String, String >> jsonOutput = null;

        log.info("URI: " + mdmsURL.toString());
        try {
            log.info(objectMapper.writeValueAsString(mdmsConfig));
            response = restTemplate.postForObject(mdmsURL.toString(), mdmsConfig, Map.class);
            jsonOutput = JsonPath.read(response, MDMS_PROPERTYTYPE_PATH);
            //migratedTenant= jsonOutput.get(0);

        } catch (ResourceAccessException e) {

            Map < String, String > map = new HashMap < > ();
            map.put(null, e.getMessage());
            throw new CustomException(map);
        } catch (HttpClientErrorException e) {

            log.info("the error is : " + e.getResponseBodyAsString());
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {

            log.error("Exception while fetching from searcher: ", e);
        }


        Set < String > usageList = new HashSet < String > ();
        for (Map < String, String > usageCat: jsonOutput) {
            usageList.add(usageCat.get("code"));
        }
        return usageList;
    }
    
    // get the payment channel and verify paymnet channel through the MDMS
    public Set < String > verifyPaymentChannel(RequestInfo requestInfo, List < Data > ingestData) {


        Boolean isPaymentChannelValid = false;
        int validCounts = 0;

        StringBuilder mdmsURL = new StringBuilder().append(mdmsHost).append(mdmsEndpoint);

        MasterDetail mstrDetail = MasterDetail.builder().name("PaymentChannel")
            .filter("[?(@.active==true)]")
            .build();


        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName("common-masters").masterDetails(Arrays.asList(mstrDetail)).build();
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId("pg").build();
        MdmsCriteriaReq mdmsConfig = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
        Object response = null;
        List < Map < String, String >> jsonOutput = null;

        log.info("URI: " + mdmsURL.toString());
        try {
            log.info(objectMapper.writeValueAsString(mdmsConfig));
            response = restTemplate.postForObject(mdmsURL.toString(), mdmsConfig, Map.class);
            jsonOutput = JsonPath.read(response, MDMS_PAYMENTCHANNEL_PATH);
            //migratedTenant= jsonOutput.get(0);

        } catch (ResourceAccessException e) {

            Map < String, String > map = new HashMap < > ();
            map.put(null, e.getMessage());
            throw new CustomException(map);
        } catch (HttpClientErrorException e) {

            log.info("the error is : " + e.getResponseBodyAsString());
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {

            log.error("Exception while fetching from searcher: ", e);
        }


        Set < String > paymentChannelList = new HashSet < String > ();
        for (Map < String, String > paymentType: jsonOutput) {
        	paymentChannelList.add(paymentType.get("code"));
        }
        return paymentChannelList;
    }
    
    // The verification logic will always use module name + date to determine the uniqueness of a set of records.
    public IngestAckData verifyIfDataAlreadyIngested(List<Data> ingestData) {
        List<String> keyDataToSearch = new ArrayList<>();
        Set<String> uniquenessHash = new HashSet<>();
        IngestAckData hashedData = new IngestAckData();
        List<AckEntity> ackEntityList = new ArrayList<>();
        
        ingestData.forEach(data -> {
            if(data.getWard().contains(":"))
        	 data.setWard(data.getWard().replace(":"," "));
            StringBuilder currKeyData = new StringBuilder();
            currKeyData.append(data.getDate()).append(":").append(data.getModule()).append(":").append(data.getWard()).append(":").append(data.getUlb()).append(":").append(data.getRegion()).append(":").append(data.getState());
            log.info("Current key data: " + currKeyData);
            if(uniquenessHash.contains(currKeyData.toString()))
                throw new CustomException("EG_DS_SAME_RECORD_ERR", "Duplicate data found in the payload");

            uniquenessHash.add(currKeyData.toString());
            keyDataToSearch.add(currKeyData.toString());
            ackEntityList.add(AckEntity.builder().datakey(currKeyData.toString()).uuid(UUID.randomUUID().toString()).build());
        });
        Boolean isRecordPresent = dataRepository.findIfRecordExists(keyDataToSearch);
        if(isRecordPresent)
            throw new CustomException("EG_DS_RECORD_ALREADY_INGESTED_ERR", "Records for the given date and area details have already been ingested. No new data will be ingested.");
        hashedData.setAckEntities(ackEntityList);

        return hashedData;
    }

    // The verification logic will always use module name + financialYear to determine the uniqueness of a set of records.
    public void verifyIfMasterDataAlreadyIngested(MasterData masterData) {
        StringBuilder uri = new StringBuilder(applicationProperties.getElasticSearchHost() + "/");
        uri.append(applicationProperties.getMasterDataIndex());
        uri.append("/_search");
        uri.append("?q=financialYear").append(":").append(masterData.getFinancialYear()).append(" AND ").append("module").append(":").append(masterData.getModule()).append(" AND ").append("region").append(":").append(masterData.getRegion()).append(" AND ").append("state").append(":").append(masterData.getState());
        log.info(uri.toString());
        Integer numOfRecordsFound = repository.findIfRecordAlreadyExists(uri);
        if (numOfRecordsFound > 0){
            throw new CustomException("EG_IDX_ERR", "Records for the given financial year and module for the given state and area details have already been ingested, input data will not be ingested.");
        }
    }

    public void validateMaxDataListSize(IngestRequest ingestRequest) {
        if(ingestRequest.getIngestData().size() > applicationProperties.getMaxDataListSize())
            throw new CustomException("EG_DS_INGEST_ERR", "Ingest service supports bulk data ingest requests of max size: " + applicationProperties.getMaxDataListSize());
    }
	
	public static String toCamelCase(String str)
	{
	    if (str == null || str.isEmpty()) {
        return str;
	    }
		str = new String (str.trim());
		StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
            
        for (char ch : str.toCharArray()) {
        	if (Character.isSpaceChar(ch)){
            convertNext = true;
        	} 
        	else if (convertNext) {
            ch = Character.toTitleCase(ch);
            convertNext = false;
        	} 
        	else {
            ch = Character.toLowerCase(ch);
        	}
        converted.append(ch);
        }
        return converted.toString();
    }

    public Map<String, Map<String, Object>> stateListMDMS(RequestInfo requestInfo) {


        StringBuilder mdmsURL = new StringBuilder().append(mdmsHost).append(mdmsEndpoint);

        MasterDetail mstrDetail = MasterDetail.builder().name("nationalInfoUser")
                .filter("[?(@.active==true)]")
                .build();

        MasterDetail mstrDetail1 = MasterDetail.builder().name("nationalInfo")
                .filter("[?(@.active==true)]")
                .build();


        ModuleDetail moduleDetail = ModuleDetail.builder().moduleName("tenant").masterDetails(Arrays.asList(mstrDetail)).build();
        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail)).tenantId("pg").build();
        MdmsCriteriaReq mdmsConfig = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria).build();
        Object response = null;
        List < Map < String, String >> nationalinfouser = null;

        log.info("URI: " + mdmsURL.toString());
        try {
            log.info(objectMapper.writeValueAsString(mdmsConfig));
            response = restTemplate.postForObject(mdmsURL.toString(), mdmsConfig, Map.class);
            nationalinfouser = JsonPath.read(response, MDMS_NATIONALTENANTUSER_PATH);


        } catch (ResourceAccessException e) {

            Map < String, String > map = new HashMap < > ();
            map.put(null, e.getMessage());
            throw new CustomException(map);
        } catch (HttpClientErrorException e) {

            log.info("the error is : " + e.getResponseBodyAsString());
            throw new ServiceCallException(e.getResponseBodyAsString());
        } catch (Exception e) {

            log.error("Exception while fetching from searcher: ", e);
        }
        ModuleDetail moduleDetail1 = ModuleDetail.builder().moduleName("tenant").masterDetails(Arrays.asList(mstrDetail1)).build();
        MdmsCriteria mdmsCriteria1 = MdmsCriteria.builder().moduleDetails(Arrays.asList(moduleDetail1)).tenantId("pg").build();
        MdmsCriteriaReq mdmsConfig1 = MdmsCriteriaReq.builder().requestInfo(requestInfo).mdmsCriteria(mdmsCriteria1).build();
        List<Map<String,String>> nationalinfo = null;
        try {
            log.info(objectMapper.writeValueAsString(mdmsConfig1));
            response = restTemplate.postForObject(mdmsURL.toString(), mdmsConfig1, Map.class);
            nationalinfo=  JsonPath.read(response, MDMS_NATIONALTENANTS_PATH);
            //migratedTenant= jsonOutput.get(0);

        } catch (ResourceAccessException e) {

            Map<String, String> map = new HashMap<>();
            map.put(null, e.getMessage());
            throw new CustomException(map);
        }  catch (HttpClientErrorException e) {

            log.info("the error is : " + e.getResponseBodyAsString());
            throw new ServiceCallException(e.getResponseBodyAsString());
        }catch (Exception e) {

            log.error("Exception while fetching from searcher: ", e);
        }

        Map<String, Map<String, Object>> stateEmailMap = new HashMap<>();

        for (Map<String, String> state : nationalinfouser) {
            String stateCode = state.get("stateCode");
            String email = state.get("emailId");
            String nodalOfficer = state.get("NodalOfficerName");
            if (stateCode != null && email != null) {
                Map<String, Object> officerInfo = new HashMap<>();
                officerInfo.put("email", email);
                officerInfo.put("nodalOfficer", nodalOfficer);
                officerInfo.put("ULBs", new ArrayList<String>());

                List<String> ulbs = (List<String>) officerInfo.get("ULBs");
                for (Map<String, String> stateUlb : nationalinfo) {
                    String stateWithUlb = stateUlb.get("stateCode");
                    String ulb = stateUlb.get("code");
                    if (stateCode.equals(stateWithUlb) && ulb != null) {
                        ulbs.add(ulb);
                    }
                }
                stateEmailMap.put(stateCode, officerInfo);
            }
        }
        return stateEmailMap;
    }
}
