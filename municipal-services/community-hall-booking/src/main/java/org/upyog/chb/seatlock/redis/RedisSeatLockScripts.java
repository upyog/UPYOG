package org.upyog.chb.seatlock.redis;

/**
 * Lua scripts: atomic acquire/renew, owner-only delete, TTL refresh for same owner.
 */
final class RedisSeatLockScripts {

	static final String ACQUIRE_OR_RENEW = """
			local current = redis.call('GET', KEYS[1])
			if current == false then
			  redis.call('SET', KEYS[1], ARGV[1], 'PX', tonumber(ARGV[2]))
			  return 1
			elseif current == ARGV[1] then
			  redis.call('PEXPIRE', KEYS[1], tonumber(ARGV[2]))
			  return 2
			else
			  return 0
			end
			""";

	static final String UNLOCK_IF_OWNER = """
			if redis.call('GET', KEYS[1]) == ARGV[1] then
			  return redis.call('DEL', KEYS[1])
			else
			  return 0
			end
			""";

	private RedisSeatLockScripts() {
	}
}
