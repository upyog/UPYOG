package org.egov.pt.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.egov.pt.models.Property;
import org.egov.pt.web.contracts.PropertyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PropertyRedisCache {

    private static final String PREFIX = "PROPERTY:";
    private static final Duration TTL = Duration.ofMinutes(30);
    
    @Autowired
    private  RedisTemplate<String, Object> redisTemplate;

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

    public void put(String tenantId, String propertyId, Property response) {
        String key = PREFIX + tenantId + ":" + propertyId;
        redisTemplate.opsForValue().set(key, response, TTL);
    }
    
   

}
