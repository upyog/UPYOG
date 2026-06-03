package digit.service;

import static digit.constants.MDMSMigrationToolkitConstants.DOT_SEPARATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.saasquatch.jsonschemainferrer.*;

import digit.config.Configuration;
import digit.repository.ServiceRequestRepository;
import digit.util.FileReader;
import digit.util.FileWriter;
import digit.web.models.SchemaDefinition;
import digit.web.models.SchemaDefinitionRequest;
import digit.web.models.SchemaMigrationRequest;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class SchemaDefinitionMigrationService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileWriter fileWriter;

    @Autowired
    private FileReader fileReader;

    @Autowired
    private JsonSchemaInferrer inferrer;
    
    @Autowired
    private Configuration config;

    @Value("${master.schema.files.dir}")
    public String schemaFilesDirectory;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    private Map<String, JsonNode> schemaCodeToSchemaJsonMap;

    public void beginMigration(SchemaMigrationRequest schemaMigrationRequest) {
        // Fetch schema code to schema definition map
        Map<String, JsonNode> schemaCodeVsSchemaDefinitionMap = fileReader.readFiles(schemaFilesDirectory);

        List<SchemaDefinition> schemaDefinitionPOJOs = new ArrayList<>();

        // Go through each schemas and generate SchemaDefinition DTOs
        schemaCodeVsSchemaDefinitionMap.keySet().forEach(schemaCode -> {
            SchemaDefinition schemaDefinition = SchemaDefinition.builder()
                    .tenantId(schemaMigrationRequest.getSchemaMigrationCriteria().getTenantId())
                    .isActive(Boolean.TRUE)
                    .code(schemaCode)
                    .definition(schemaCodeVsSchemaDefinitionMap.get(schemaCode))
                    .id(UUID.randomUUID().toString())
                    .build();
            schemaDefinitionPOJOs.add(schemaDefinition);
        });

        schemaDefinitionPOJOs.forEach(schemaDefinition -> {
            SchemaDefinitionRequest schemaDefinitionRequest = SchemaDefinitionRequest.builder()
                    .requestInfo(schemaMigrationRequest.getRequestInfo())
                    .schemaDefinition(schemaDefinition)
                    .build();

            // Send it to kafka/make API calls to MDMS service schema APIs
            try {
            	serviceRequestRepository.fetchResult(new StringBuilder(config.getMdmsV2Host() + config.getMdmsV2SchemaCreateEndPoint()), schemaDefinitionRequest);
			} catch (Exception e) {
				log.error("Error in : " + schemaDefinition);
			}
        });
    }


    public void generateSchemaDefinition() {
        Map<String, Map<String, Map<String, JSONArray>>> tenantMap = MDMSApplicationRunnerImpl.getTenantMap();
        
        Map<String, Map<String, Object>> masterConfigMap = MDMSApplicationRunnerImpl.getMasterConfigMap();

        schemaCodeToSchemaJsonMap = new HashMap<>();

        // Traverse tenantMap across the tenants, modules and masters to generate schema for each master
        tenantMap.keySet().forEach(tenantId -> {
            tenantMap.get(tenantId).keySet().forEach(module -> {
                tenantMap.get(tenantId).get(module).keySet().forEach(master -> {
                    JSONArray masterDataJsonArray = MDMSApplicationRunnerImpl
                            .getTenantMap()
                            .get(tenantId)
                            .get(module)
                            .get(master);

                    if (!masterDataJsonArray.isEmpty()) {
                        // Convert master data to JsonNode
//                        JsonNode jsonNode = objectMapper.convertValue(masterDataJsonArray.get(0), JsonNode.class);

                        // Feed the converted master data to jsonSchemaInferrer for generating schema
//                        JsonNode schemaNode = inferrer.inferForSample(jsonNode);
                        
                        try {	
                        	ObjectNode schemaNode = getSchemaNode(masterDataJsonArray);
                        	if(!schemaNode.get("properties").has("id")) {
                        		addIdFieldInSchema(schemaNode);
                            }
                        	
                        	// Populate schemaCodeToSchemaJsonMap
                            schemaCodeToSchemaJsonMap.put(module + DOT_SEPARATOR + master, schemaNode);

                            // Write generated schema definition to files with the name in module.master format
                            fileWriter.writeJsonToFile(schemaNode, module + DOT_SEPARATOR + master);
                        	
						} catch (Exception e) {
							log.error("Error in : " + module + DOT_SEPARATOR + master + " - " + e.getMessage());
						}
                        
                    }

                });
            });
        });
    }
    
    /**
     * Add ID Field in the Master Data Schema
     *  
     * @param schemaNode
     * @throws Exception
     */
    private void addIdFieldInSchema(JsonNode schemaNode)throws Exception {
    	ObjectNode properties = (ObjectNode)schemaNode.get("properties");
    	JsonNode typeNode = objectMapper.readTree("{\"type\":\"string\"}");
    	properties.set("id", typeNode);
    	
    	ArrayNode required = (ArrayNode)schemaNode.get("required");
    	required.add("id");
    }
    
    /**
     * Generate Master Data Schema
     * 
     * @param masterDataJsonArray
     * @return
     */
    private ObjectNode getSchemaNode(JSONArray masterDataJsonArray){
    	
    	//Get Master Data Size
    	Long dataSize = Long.valueOf(masterDataJsonArray.size());
    	
    	//Get All the master data Entries
    	List<Entry<String, Object>> entryList = (List<Map.Entry<String, Object>>)masterDataJsonArray
    			.stream()
    			.map(data -> objectMapper.convertValue(data, HashMap.class))
    			.flatMap(dataMap -> dataMap.entrySet().stream()).collect(Collectors.toList());
    	    	
    	//Get Master data that contains all the Unique keys
    	Map<String, Object> dataMap = entryList.stream().distinct().filter(entry -> entry.getValue() != null)
    			.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (oldValue, newValue) -> newValue));
    	
    	entryList.stream().distinct()
    	.filter(entry -> entry.getValue() == null)
    	.map(entry -> entry.getKey()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
    	.entrySet().stream().filter(entry -> entry.getValue().equals(dataSize))
    	.forEach(entry -> dataMap.put(entry.getKey(), null));
    	
    	//Get required fields for Master Data
    	List<String> requiredList = entryList.stream()
    			.map(Entry::getKey)
    			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
    			.entrySet().stream().filter(entry -> entry.getValue().equals(dataSize))
    			.map(Entry::getKey).collect(Collectors.toList());
    	
    	 // Convert master data to JsonNode
        JsonNode jsonNode = objectMapper.convertValue(dataMap, JsonNode.class);

        // Feed the converted master data to jsonSchemaInferrer for generating schema
        Map<String, Object> schemaNodeMap = objectMapper.convertValue(inferrer.inferForSample(jsonNode), Map.class);
        schemaNodeMap.put("required", requiredList);
        schemaNodeMap.put("x-unique", Arrays.asList("id"));
                
        return objectMapper.convertValue(schemaNodeMap, ObjectNode.class);
    }
    
}
