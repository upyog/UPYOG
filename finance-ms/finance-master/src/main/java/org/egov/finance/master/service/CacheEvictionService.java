/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.master.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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

	public void evictTenantCache(String tenantId, int versionThreshold, String cacheName) {
		String pattern = cacheName + MasterConstants.REDIS_SEARCH_TENANT_TAG + tenantId
				+ MasterConstants.REDIS_SEARCH_VERSION_TAG + "v*";
		log.info("Patterns to be deleted: {}", pattern);

		redisTemplate.execute((RedisCallback<Void>) connection -> {
			RedisKeyCommands keyCommands = connection.keyCommands();
			List<byte[]> keysToDelete = scanKeysToDelete(keyCommands, pattern, versionThreshold);

			if (!keysToDelete.isEmpty()) {
				keysToDelete.forEach(keyCommands::del);
				log.info("Deleted {} keys with version < v{}", keysToDelete.size(), versionThreshold);
			}

			return null;
		});
	}

	private List<byte[]> scanKeysToDelete(RedisKeyCommands keyCommands, String pattern, int versionThreshold) {
		List<byte[]> keysToDelete = new ArrayList<>();
		ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();

		try (Cursor<byte[]> cursor = keyCommands.scan(options)) {
			while (cursor.hasNext()) {
				byte[] keyBytes = cursor.next();
				String key = new String(keyBytes, StandardCharsets.UTF_8);

				if (isVersionBelowThreshold(key, versionThreshold)) {
					keysToDelete.add(keyBytes);
					log.info("Marked for deletion: {}", key);
				}
			}
		} catch (Exception e) {
			log.error("Error scanning Redis keys", e);
		}

		return keysToDelete;
	}

	private boolean isVersionBelowThreshold(String key, int threshold) {
		Matcher matcher = Pattern.compile("version=v(\\d+)").matcher(key);
		return matcher.find() && Integer.parseInt(matcher.group(1)) < threshold;
	}

	public String getVersionForTenant(String tenantId, String cacheKeyName) {
		String key = cacheKeyName + tenantId;
		String version = stringRedisTemplate.opsForValue().get(key);
		return version != null ? version : MasterConstants.REDIS_START_VERSION_V1;
	}

	public void incrementVersionForTenant(String tenantId, String cacheKeyName, String cacheName) {
		String versionKey = cacheKeyName + tenantId;
		String currentVersion = stringRedisTemplate.opsForValue().get(versionKey);

		if (currentVersion == null) {
			stringRedisTemplate.opsForValue().set(versionKey, "v1");
			return;
		}

		int versionNumber;
		try {
			versionNumber = Integer.parseInt(currentVersion.replace("v", ""));
		} catch (NumberFormatException e) {
			versionNumber = 1;
		}

		String newVersion = "v" + (versionNumber + 1);
		stringRedisTemplate.opsForValue().set(versionKey, newVersion);
		evictTenantCache(tenantId, versionNumber, cacheName);
	}

}
