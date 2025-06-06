/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.master.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CacheEvictionService {

	private RedisTemplate<String, Object> redisTemplate;

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public CacheEvictionService(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public void evictTenantCache(String tenantId) {
		String pattern = "*::tenant=" + tenantId;

		redisTemplate.execute((RedisCallback<Void>) connection -> {
			RedisKeyCommands keyCommands = connection.keyCommands();
			ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
			List<byte[]> keysToDelete = new ArrayList<>();

			try (var cursor = keyCommands.scan(options)) {
				while (cursor.hasNext()) {
					keysToDelete.add(cursor.next());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!keysToDelete.isEmpty()) {
				for (byte[] key : keysToDelete) {
					keyCommands.del(key);
				}
			}
			return null;
		});
	}

	public String getVersionForTenant(String tenantId,String cacheKeyName) {
		String key = cacheKeyName + tenantId;
		String version = stringRedisTemplate.opsForValue().get(key);
		return version != null ? version : "v1";
	}

	public void incrementVersionForTenant(String tenantId,String cacheKeyName) {
		String key = cacheKeyName + tenantId;
		Boolean exists = stringRedisTemplate.hasKey(key);

		String currentVersion;
		if (Boolean.FALSE.equals(exists)) {
			currentVersion = "v1";
			stringRedisTemplate.opsForValue().set(key, currentVersion);
			return;
		} else {
			currentVersion = stringRedisTemplate.opsForValue().get(key);
			if (currentVersion == null) {
				currentVersion = "v1";
				stringRedisTemplate.opsForValue().set(key, currentVersion);
				return;
			}
		}

		int versionNumber = Integer.parseInt(currentVersion.replace("v", ""));
		String newVersion = "v" + (versionNumber + 1);
		stringRedisTemplate.opsForValue().set(key, newVersion);

		String oldVersion = "v" + versionNumber;
		Set<String> keysToDelete = redisTemplate.keys("fundSearchCache::version=" + oldVersion + "::*");
		if (keysToDelete != null && !keysToDelete.isEmpty()) {
			redisTemplate.delete(keysToDelete);
		}
	}

}
