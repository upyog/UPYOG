/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.report.util;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@EnableCaching
@Configuration
public class RedisCacheConfig {

	
	  @Bean public RedisCacheConfiguration cacheConfiguration() { ObjectMapper
	  objectMapper = new ObjectMapper();
	  objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	  objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	  false);
	  
	  Jackson2JsonRedisSerializer<Object> jacksonSerializer = new
	  Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
	  
	  return
	  RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
	  .serializeValuesWith(RedisSerializationContext.SerializationPair.
	  fromSerializer(jacksonSerializer)) .disableCachingNullValues(); }
	 


	
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfiguration()).build();
	}
	
	
	  @Bean public RedisTemplate<String, Object>
	  redisTemplate(RedisConnectionFactory connectionFactory) {
	  RedisTemplate<String, Object> template = new RedisTemplate<>();
	  template.setConnectionFactory(connectionFactory);
	  
	  ObjectMapper objectMapper = new ObjectMapper();
	  objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	  objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	  objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
	  false);
	  
	  Jackson2JsonRedisSerializer<Object> serializer = new
	  Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
	  
	  template.setKeySerializer(new StringRedisSerializer());
	  template.setValueSerializer(serializer); template.setHashKeySerializer(new
	  StringRedisSerializer()); template.setHashValueSerializer(serializer);
	  
	  template.afterPropertiesSet(); return template; }
	 

}
