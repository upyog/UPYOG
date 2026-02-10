package org.egov.pt.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Assessment;
import org.egov.pt.models.AssessmentSearchCriteria;
import org.egov.pt.models.Property;
import org.egov.pt.models.collection.Payment;
import org.egov.pt.service.AssessmentService;
import org.egov.pt.web.contracts.PropertyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PropertyRedisCache {

    public static final String PREFIX = "PROPERTY:";
    public static final String PREFIX_PAYMENT = "PAYMENT:";
    private static final Duration TTL = Duration.ofMinutes(30);
    
    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    AssessmentService assessmentService;

    public PropertyRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<String, Property> multiGet(Map<String, String> propertyTenantMap) {

    	List<String> keys = propertyTenantMap.entrySet().stream()
    	        .map(e -> PREFIX + e.getValue() + ":" + e.getKey())
    	        .collect(Collectors.toList());
    	
    	 List<Map.Entry<String, String>> entries =
    	            new ArrayList<>(propertyTenantMap.entrySet());
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);

        Map<String, Property> result = new HashMap<>();

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                Object val = values.get(i);
                if (val != null) {
                    String propertyId = entries.get(i).getKey();
                    result.put(propertyId, (Property) val);
                    redisTemplate.expire(keys.get(i), TTL.toMinutes(), TimeUnit.MINUTES);
                }
            }
        }
        return result;
    }
    
    
    
    public Map<String, List<Payment>> multiGetPayment(
            Map<String, String> propertyTenantMap) {

        List<Map.Entry<String, String>> entries =
                new ArrayList<>(propertyTenantMap.entrySet());

        List<String> keys = entries.stream()
                .map(e -> PREFIX_PAYMENT + e.getValue() + ":" + e.getKey())
                .collect(Collectors.toList());

        List<Object> values = redisTemplate.opsForValue().multiGet(keys);

        Map<String, List<Payment>> result = new HashMap<>();

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                Object val = values.get(i);

                if (val instanceof List<?>) {
                    String propertyId = entries.get(i).getKey();

                    @SuppressWarnings("unchecked")
                    List<Payment> payments = (List<Payment>) val;

                    result.put(propertyId, payments);

                    redisTemplate.expire(
                            keys.get(i),
                            TTL.toMinutes(),
                            TimeUnit.MINUTES
                    );
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<Assessment>> getAssessmentsForProperties(Set<String> propertyIds,RequestInfo requestInfo) {

        Map<String, List<Assessment>> assesments = new LinkedHashMap<>();
        Set<String> missingPropertyIds = new HashSet<String>();

        
        for (String propertyId : propertyIds) {
            String key = "pt:assessment:byProperty:" + propertyId;
            Object cachedObj = redisTemplate.opsForValue().get(key);

            if (cachedObj != null) {
                
                List<Assessment> cached = (List<Assessment>) cachedObj;
                assesments.put(propertyId, cached);
            } else {
                missingPropertyIds.add(propertyId);
            }
        }

        if (!missingPropertyIds.isEmpty()) {
        	
        	 AssessmentSearchCriteria assessmentSearchCriteria=AssessmentSearchCriteria.builder().propertyIds(missingPropertyIds).build();
        	
            List<Assessment> dbAssessments = assessmentService.searchAssessments(assessmentSearchCriteria, requestInfo);
            		//searchAssessments(missingPropertyIds);

            // Group by propertyId
            Map<String, List<Assessment>> grouped = dbAssessments.stream()
                .collect(Collectors.groupingBy(Assessment::getPropertyId, LinkedHashMap::new, Collectors.toList()));

            // 3️⃣ Populate Redis and result map
            for (String pid : missingPropertyIds) {
                List<Assessment> list = grouped.getOrDefault(pid, Collections.emptyList());

                // Write-through cache
                String key = "pt:assessment:byProperty:" + pid;
                redisTemplate.opsForValue().set(key, list, TTL);

                assesments.put(pid, list);
            }
        }

        // 4️⃣ Ensure all propertyIds exist in map (even empty list)
        for (String pid : propertyIds) {
            assesments.putIfAbsent(pid, Collections.emptyList());
        }

        return assesments;
    }


    public void put(String tenantId, String propertyId, Property response) {
        String key = PREFIX + tenantId + ":" + propertyId;
        redisTemplate.opsForValue().set(key, response, TTL);
    }
    
    
    public void put(String key, List<Payment> payments) {
  
        redisTemplate.opsForValue().set(key, payments, TTL);
    }
    
   

}
