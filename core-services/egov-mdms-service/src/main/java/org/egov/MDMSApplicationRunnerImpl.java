package org.egov;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Component
@Slf4j
public class MDMSApplicationRunnerImpl {

    @Autowired
    public ResourceLoader resourceLoader;

    @Value("${egov.mdms.conf.path}")
    public String mdmsFileDirectory;

    @Value("${masters.config.url}")
    public String masterConfigUrl;

    @Value("${egov.mdms.stopOnAnyConfigError:true}")
    public boolean stopOnAnyConfigError;

    private static Map<String, Map<String, Map<String, JSONArray>>> tenantMap = new HashMap<>();

    private static Map<String, Map<String, Object>> masterConfigMap = new HashMap<>();

    ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void run() {
        try {
            log.info("Reading files from: " + mdmsFileDirectory);
            LinkedList<String> errorFilesList = new LinkedList<>();
            readMdmsConfigFiles(masterConfigUrl);
            readFiles(mdmsFileDirectory, errorFilesList);
            log.info("List Of Files which has Error while parsing " + errorFilesList);
            if (!errorFilesList.isEmpty() && stopOnAnyConfigError) {
                log.info("Stopping as all files could not be loaded");
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("Exception while loading yaml files: ", e);
        }

    }

    public void readFiles(String baseFolderPath, LinkedList<String> errorList) {
        File folder = new File(baseFolderPath);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    String name = file.getName();
                    String fileExtension = FilenameUtils.getExtension(file.getAbsolutePath()).toLowerCase();

                    if (fileExtension.equalsIgnoreCase("json") || fileExtension.equalsIgnoreCase("yaml")
                            || fileExtension.equalsIgnoreCase("yml")) {
                        log.debug("Reading file....:- " + file.getAbsolutePath());
                        try {
                            Map<String, Object> jsonMap = objectMapper.readValue(file,
                                    new TypeReference<Map<String, Object>>() {
                                        @Override
                                        public java.lang.reflect.Type getType() {
                                            return super.getType();
                                        }
                                    });
                            prepareTenantMap(jsonMap);
                        } catch (Exception e) {
                            log.error("Error occurred while loading file", e);
                            errorList.add(file.getAbsolutePath());
                        }
                    }
                } else if (file.isDirectory()) {
                    readFiles(file.getAbsolutePath(), errorList);
                }
            }
        }
    }

    public void prepareTenantMap(Map<String, Object> map) {
        String tenantId = (String) map.get("tenantId");
        String moduleName = (String) map.get("moduleName");

        // Remove non-master keys
        map.remove("tenantId");
        map.remove("moduleName");

        if (!tenantMap.containsKey(tenantId)) {
            tenantMap.put(tenantId, new HashMap<>());
        }

        Map<String, Map<String, JSONArray>> tenantModule = tenantMap.get(tenantId);

        if (!tenantModule.containsKey(moduleName)) {
            tenantModule.put(moduleName, new HashMap<>());
        }

        Map<String, JSONArray> masterDataMap = tenantModule.get(moduleName);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String masterName = entry.getKey();
            Object masterData = entry.getValue();

            if (masterData instanceof JSONArray) {
                masterDataMap.put(masterName, (JSONArray) masterData);
            } else {
                log.error("Master data is not a JSONArray: " + masterName);
            }
        }
    }

    public void readMdmsConfigFiles(String masterConfigUrl) throws Exception {
        log.info("Loading master configs from: " + masterConfigUrl);
        Resource resource = resourceLoader.getResource(masterConfigUrl);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            masterConfigMap = objectMapper.readValue(inputStream,
                    new TypeReference<Map<String, Map<String, Object>>>() {
                    });
        } catch (IOException e) {
            log.error("Exception while fetching service map for: ", e);
            log.error("Incorrect format of the file: " + masterConfigUrl);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        log.info("the Master config Map : " + masterConfigMap);
    }

    public static Map<String, Map<String, Map<String, JSONArray>>> getTenantMap() {
        return tenantMap;
    }

    public static Map<String, Map<String, Object>> getMasterConfigMap() {
        return masterConfigMap;
    }

}
