/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.master.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.egov.finance.master.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheEvictionService {

	

	private RedisTemplate<String, Object> redisTemplate;

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public CacheEvictionService(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public void evictTenantCache(String tenantId,int versionThreshold,String cacheName) {
		String pattern = cacheName + MasterConstants.REDIS_SEARCH_TENANT_TAG + tenantId + MasterConstants.REDIS_SEARCH_VERSION_TAG+"v*";
		log.info("patterns to be deleted :{}",pattern);
	    redisTemplate.execute((RedisCallback<Void>) connection -> {
	        RedisKeyCommands keyCommands = connection.keyCommands();
	        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
	        List<byte[]> keysToDelete = new ArrayList<>();

	        try (Cursor<byte[]> cursor = keyCommands.scan(options)) {
	            while (cursor.hasNext()) {
	                byte[] keyBytes = cursor.next();
	                String key = new String(keyBytes, StandardCharsets.UTF_8);

	                // Extract version=vX
	                Matcher matcher = Pattern.compile("version=v(\\d+)").matcher(key);
	                if (matcher.find()) {
	                    int version = Integer.parseInt(matcher.group(1));
	                    if (version < versionThreshold) {
	                        keysToDelete.add(keyBytes);
	                        log.info("Marked for deletion: {}", key);
	                    }
	                }
	            }
	        } catch (Exception e) {
	            log.error("Error scanning Redis keys", e);
	        }

	        if (!keysToDelete.isEmpty()) {
	            for (byte[] key : keysToDelete) {
	                keyCommands.del(key);
	            }
	            log.info("Deleted {} keys with version <= v{}", keysToDelete.size(), versionThreshold);
	        }

	        return null;
	    });
	}

	public String getVersionForTenant(String tenantId,String cacheKeyName) {
		String key = cacheKeyName + tenantId;
		String version = stringRedisTemplate.opsForValue().get(key);
		return version != null ? version : MasterConstants.REDIS_START_VERSION_V1;
	}

	public void incrementVersionForTenant(String tenantId,String cacheKeyName,String cacheName) {
		String key = cacheKeyName + tenantId;
		Boolean exists = stringRedisTemplate.hasKey(key);

		String currentVersion;
		if (Boolean.FALSE.equals(exists)) {
			currentVersion = MasterConstants.REDIS_START_VERSION_V1;
			stringRedisTemplate.opsForValue().set(key, currentVersion);
			return;
		} else {
			currentVersion = stringRedisTemplate.opsForValue().get(key);
			if (currentVersion == null) {
				currentVersion = MasterConstants.REDIS_START_VERSION_V1;
				stringRedisTemplate.opsForValue().set(key, currentVersion);
				return;
			}
		}

		int versionNumber = Integer.parseInt(currentVersion.replace("v", ""));
		String newVersion = "v" + (versionNumber + 1);
		stringRedisTemplate.opsForValue().set(key, newVersion);
		
		String oldVersion = "v" + versionNumber;
		Set<String> keysToDelete = redisTemplate.keys(cacheName+MasterConstants.REDIS_SEARCH_VERSION_TAG + oldVersion + "::*");
		if (keysToDelete != null && !keysToDelete.isEmpty()) {
			System.out.println(keysToDelete);
			//redisTemplate.delete(keysToDelete);
		}
		evictTenantCache(tenantId,versionNumber,cacheName);
	}
	
	

}
