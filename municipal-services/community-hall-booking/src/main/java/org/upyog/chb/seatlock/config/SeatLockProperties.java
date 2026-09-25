package org.upyog.chb.seatlock.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;


/**
 *
 * Immutable configuration (record + compact constructor) — Boot 3 binds by constructor parameter names.
 *
 * provider: Determines the primary storage mechanism for seat locks. It can be set to "db" for database storage, "redis" for Redis storage, or "redis-with-db-fallback"
 * to use Redis with a fallback to the database if Redis is unavailable.
 *
 * defaultLockTtl: Specifies the default time-to-live (TTL) duration for seat locks. This is the amount of time a lock will remain valid before it expires automatically.
 *
 * redisKeyPrefix: Defines the prefix to be used for keys in Redis when storing seat lock information. This helps in organizing and identifying related keys in Redis.
 * <p>
 * rateLimitEnabled: A boolean flag that indicates whether rate limiting is enabled for seat lock operations. If true, the system will enforce limits on how many locks can
 * be created within a certain time frame.
 *
 * maxLocksPerMinute: Specifies the maximum number of locks that can be created per minute when rate limiting is enabled. This helps prevent abuse and ensures fair
 * usage of the locking mechanism.
 *
 * cooldownAfterLocks: Defines the number of locks that, once reached, will trigger a cooldown period during which no new locks can be created by the same user or IP address.
 *
 * cooldownDuration: Specifies the duration of the cooldown period that is triggered after reaching the maxLocksPerMinute threshold.
 * During this time, no new locks can be created by the affected user or IP address.
 *
 * banAfterLocks: Defines the number of locks that, once reached, will trigger a ban on creating new locks for a specified duration.
 * This is a more severe measure than cooldown and is intended to prevent repeated abuse.
 *
 * banDuration: Specifies the duration of the ban that is triggered after reaching the banAfterLocks threshold. During this time,
 * no new locks can be created by the affected user or IP address.
 */
@ConfigurationProperties(prefix = "seat-lock")
public record SeatLockProperties(
		@DefaultValue("db") String provider,
		@DefaultValue("PT5M") Duration defaultLockTtl,
		@DefaultValue("seat:") String redisKeyPrefix,
		@DefaultValue("true") boolean rateLimitEnabled,
		@DefaultValue("5") int maxLocksPerMinute,
		@DefaultValue("10") int cooldownAfterLocks,
		@DefaultValue("PT2M") Duration cooldownDuration,
		@DefaultValue("20") int banAfterLocks,
		@DefaultValue("PT30M") Duration banDuration) {

	public boolean redisWithDbFallback() {
		return "redis-with-db-fallback".equalsIgnoreCase(provider);
	}

	public boolean useRedisPrimary() {
		return "redis".equalsIgnoreCase(provider) || redisWithDbFallback();
	}
}
