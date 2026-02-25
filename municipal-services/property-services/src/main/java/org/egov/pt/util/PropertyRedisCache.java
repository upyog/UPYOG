package org.egov.pt.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Appeal;
import org.egov.pt.models.AppealCriteria;
import org.egov.pt.models.Assessment;
import org.egov.pt.models.AssessmentSearchCriteria;
import org.egov.pt.models.Property;
import org.egov.pt.models.collection.Payment;
import org.egov.pt.models.collection.RevenuDataBucket;
import org.egov.pt.service.AppealService;
import org.egov.pt.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PropertyRedisCache {

    public static final String PREFIX = "PROPERTY:";
    public static final String PREFIX_PAYMENT = "PAYMENT:";
    public static final String PREFIX_PENALTY = "PENALTY:";
    private static final Duration TTL = Duration.ofMinutes(30);
    
    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    AssessmentService assessmentService;
    
    @Autowired
    AppealService appealService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    
    
    public Map<String, List<RevenuDataBucket>> multiGetPenalty(
            Map<String, String> propertyTenantMap) {

        List<Map.Entry<String, String>> entries =
                new ArrayList<>(propertyTenantMap.entrySet());

        List<String> keys = entries.stream()
                .map(e -> PREFIX_PENALTY + e.getValue() + ":" + e.getKey())
                .collect(Collectors.toList());

        List<Object> values = redisTemplate.opsForValue().multiGet(keys);

        Map<String, List<RevenuDataBucket>> result = new HashMap<>();

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                Object val = values.get(i);

                if (val instanceof List<?>) {
                    String propertyId = entries.get(i).getKey();

                    @SuppressWarnings("unchecked")
                    List<RevenuDataBucket> datalist = (List<RevenuDataBucket>) val;

                    result.put(propertyId, datalist);

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
    
    @SuppressWarnings("unchecked")
    public Map<String, List<Appeal>> getAppealsForProperties(Set<String> propertyIds) {

        Map<String, List<Appeal>> appeals = new LinkedHashMap<>();
        Set<String> missingPropertyIds = new HashSet<String>();

        
        for (String propertyId : propertyIds) {
            String key = "pt:appeal:byProperty:" + propertyId;
            Object cachedObj = redisTemplate.opsForValue().get(key);

            if (cachedObj != null) {
                
                List<Appeal> cached = (List<Appeal>) cachedObj;
                appeals.put(propertyId, cached);
            } else {
                missingPropertyIds.add(propertyId);
            }
        }

        if (!missingPropertyIds.isEmpty()) {
        	
        	 AppealCriteria appealCriteria=AppealCriteria.builder().propertyIds(missingPropertyIds).build();
        	
            List<Appeal> dbAppeals = appealService.searchAppeal(appealCriteria);
            		//searchAssessments(missingPropertyIds);

            // Group by propertyId
            Map<String, List<Appeal>> grouped = dbAppeals.stream()
                .collect(Collectors.groupingBy(Appeal::getPropertyId, LinkedHashMap::new, Collectors.toList()));

            // 3️⃣ Populate Redis and result map
            for (String pid : missingPropertyIds) {
                List<Appeal> list = grouped.getOrDefault(pid, Collections.emptyList());

                // Write-through cache
                String key = "pt:appeal:byProperty:" + pid;
                redisTemplate.opsForValue().set(key, list, TTL);

                appeals.put(pid, list);
            }
        }

        // 4️⃣ Ensure all propertyIds exist in map (even empty list)
        for (String pid : propertyIds) {
        	appeals.putIfAbsent(pid, Collections.emptyList());
        }

        return appeals;
    }



    public void put(String tenantId, String propertyId, Property response) {
        String key = PREFIX + tenantId + ":" + propertyId;
        redisTemplate.opsForValue().set(key, response, TTL);
    }
    
    
    public  <T> void put(String key, List<T> data) {
  
        redisTemplate.opsForValue().set(key, data, TTL);
    }
    
    public void putAll(Map<String, List<RevenuDataBucket>> data) {

        if (data == null || data.isEmpty()) return;
        
        List<String> keys = new ArrayList<>(data.keySet());
        List<Object> existingValues = redisTemplate.opsForValue().multiGet(keys);
        Map<String, List<RevenuDataBucket>> missingKeys = new HashMap<>();
        
        if (existingValues != null) {
            for (int i = 0; i < keys.size(); i++) {
                if (existingValues.get(i) == null) {
                    missingKeys.put(keys.get(i), data.get(keys.get(i)));
                }
            }
        } else {
            missingKeys.putAll(data);
        }

        if (missingKeys.isEmpty()) {
            return;
        }

        long ttlSeconds = TTL.getSeconds();
        
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {

            RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
            @SuppressWarnings("unchecked")
            RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

            for (Map.Entry<String, List<RevenuDataBucket>> entry : missingKeys.entrySet()) {

                byte[] key = keySerializer.serialize(entry.getKey());
                byte[] value = valueSerializer.serialize(entry.getValue());

                connection.set(
                        key,
                        value,
                        Expiration.seconds(ttlSeconds),
                        RedisStringCommands.SetOption.UPSERT
                );
            }

            return null;
        });
    }
    
}
