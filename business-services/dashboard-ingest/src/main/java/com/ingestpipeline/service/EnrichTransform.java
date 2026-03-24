package com.ingestpipeline.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingestpipeline.util.ConfigLoader;

@Service
public class EnrichTransform {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(EnrichTransform.class);

    @Autowired
    private TransformService transformService;
    
    @Autowired
    private ConfigLoader configLoader; 

    private static final String SEPARATOR = "_";
    private static final String JSON_EXTENSION = ".json";
    private static final String OBJECTIVE = "transform";
    private static final String CONFIGROOT = "config/";
    private static final String VERSION = "v1";
    private static final String ENHANCE = "enhance";



    /**
     * Tranforms domain raw response from elastic search
     * This transformation is specific to domain objects
     *
     * @param rawResponseNode
     * @param businessService
     * @return
     */
    public Object transform (Map rawResponseNode, String businessService) throws IOException {
    	
    	ObjectMapper mapper = new ObjectMapper(); 
		List chainrSpecJSON = null ;
        Object transNode = null;
		try {
			//logs
			LOGGER.info("rawResponseNode keys :: " + rawResponseNode.keySet());
			LOGGER.info("rawResponseNode :: " + rawResponseNode);
			
            chainrSpecJSON = mapper.readValue(configLoader.get(OBJECTIVE.concat(SEPARATOR).concat(businessService.toLowerCase()).concat(SEPARATOR).concat(VERSION).concat(JSON_EXTENSION)), List.class);
            LOGGER.info("ChainrSpecJSON::" + chainrSpecJSON);
            Chainr chainr = Chainr.fromSpec( chainrSpecJSON );
            
            //log
            LOGGER.info("Contains _source at root? :: " + rawResponseNode.containsKey("_source"));
            Object indexData = rawResponseNode.keySet().contains("_source") ? ((Map)rawResponseNode.get("_source")).get("Data") : null;

            //logs
            LOGGER.info("indexData from current logic :: " + indexData);
            try {
                Map hitsMap = (Map) rawResponseNode.get("hits");
                LOGGER.info("hitsMap :: " + hitsMap);

                if (hitsMap != null) {
                    List hitsList = (List) hitsMap.get("hits");
                    LOGGER.info("hitsList size :: " + (hitsList != null ? hitsList.size() : "null"));

                    if (hitsList != null && !hitsList.isEmpty()) {
                        Map firstHit = (Map) hitsList.get(0);
                        LOGGER.info("firstHit :: " + firstHit);

                        Map source = (Map) firstHit.get("_source");
                        LOGGER.info("actual _source :: " + source);

                        Object actualData = source.get("Data");
                        LOGGER.info("actual Data node :: " + actualData);
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Error while logging ES structure", ex);
            }
            
            transNode = indexData!= null ? chainr.transform(indexData) : null;

		} catch (Exception e) {
			LOGGER.error("Encountered an error : businessService {} ", e.getMessage());
		}

        return transNode;

    }
    
    
    /**
     * Tranforms domain raw response from elastic search
     * This transformation is specific to domain objects
     *
     * @param rawResponseNode
     * @param businessService
     * @return
     */
    public Object transformEnhanceData (List rawResponseList, String businessService) throws IOException {
    	
    	ObjectMapper mapper = new ObjectMapper(); 
		List chainrSpecJSON = null ;
        Object transNode = null;
        List response = new ArrayList();
        
		try {
            chainrSpecJSON = mapper.readValue(configLoader.get(OBJECTIVE.concat(SEPARATOR).concat(businessService.toLowerCase()).concat(SEPARATOR).concat(ENHANCE).concat(SEPARATOR).concat(VERSION).concat(JSON_EXTENSION)), List.class);
            LOGGER.info("ChainrSpecJSON::" + chainrSpecJSON);
            Chainr chainr = Chainr.fromSpec( chainrSpecJSON );
            
            for (Object object : rawResponseList) {
            	if(object instanceof Map) {
            		Map rawResponseNode = (Map) object;
            		Object indexData = rawResponseNode.keySet().contains("_source") ? ((Map)rawResponseNode.get("_source")).get("Data") : null;
                    transNode = indexData!= null ? chainr.transform(indexData) : null;
                    response.add(transNode);
            	}
				
			}

		} catch (Exception e) {
			LOGGER.error("Encountered in transformEnhanceData with an error : businessService {} ", e.getMessage());
		}

        return response;

    }
    
}
