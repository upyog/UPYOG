package org.upyog.chb.seatlock.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.upyog.chb.seatlock.api.SeatLockService;
import org.upyog.chb.seatlock.composite.CompositeSeatLockService;
import org.upyog.chb.seatlock.db.DbSeatLockRepository;
import org.upyog.chb.seatlock.db.DbSeatLockService;
import org.upyog.chb.seatlock.ratelimit.NoOpSeatLockRateLimiter;
import org.upyog.chb.seatlock.ratelimit.RedisSeatLockRateLimiter;
import org.upyog.chb.seatlock.ratelimit.SeatLockRateLimiter;
import org.upyog.chb.seatlock.redis.RedisSeatLockService;

/**
 * Legacy generic seat-lock module — disabled by default. CHB payment timers use {@code eg_chb_payment_timer} only.
 */
@Configuration
@ConditionalOnProperty(name = "seat.lock.enabled", havingValue = "true")
@EnableConfigurationProperties(SeatLockProperties.class)
public class SeatLockConfiguration {

	@Bean
	public SeatLockRateLimiter seatLockRateLimiter(SeatLockProperties properties,
			ObjectProvider<StringRedisTemplate> redisTemplate) {
		var redis = redisTemplate.getIfAvailable();
		if (properties.rateLimitEnabled() && redis != null) {
			return new RedisSeatLockRateLimiter(redis, properties);
		}
		return new NoOpSeatLockRateLimiter();
	}

	@Bean
	@ConditionalOnProperty(name = "seat-lock.provider", havingValue = "redis")
	@ConditionalOnBean(StringRedisTemplate.class)
	public SeatLockService redisSeatLockService(StringRedisTemplate redisTemplate, SeatLockProperties properties) {
		return new RedisSeatLockService(redisTemplate, properties);
	}

	@Bean
	@ConditionalOnProperty(name = "seat-lock.provider", havingValue = "db", matchIfMissing = true)
	public SeatLockService dbSeatLockService(DbSeatLockRepository repository) {
		return new DbSeatLockService(repository);
	}

	@Bean
	@ConditionalOnProperty(name = "seat-lock.provider", havingValue = "redis-with-db-fallback")
	@ConditionalOnBean(StringRedisTemplate.class)
	public SeatLockService compositeSeatLockService(StringRedisTemplate redisTemplate, SeatLockProperties properties,
			DbSeatLockRepository repository) {
		var redis = new RedisSeatLockService(redisTemplate, properties);
		var db = new DbSeatLockService(repository);
		return new CompositeSeatLockService(redis, db);
	}
}
