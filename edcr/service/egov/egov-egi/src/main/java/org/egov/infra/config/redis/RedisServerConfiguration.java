package org.egov.infra.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.List;

/**
 * Spring configuration for Redis connectivity (standalone or sentinel mode).
 *
 * <p>Configuration notes:</p>
 * <ul>
 *   <li>Embedded Redis server bean ({@code EmbeddedRedisServer}) is disabled by default.
 *       Set {@code redis.enable.embedded=true} in {@code egov-erp-<username>.properties}
 *       for local Linux/macOS development, or configure a standalone Redis instance.</li>
 *   <li>Uses {@link JedisConnectionFactory} with Spring Data Redis for session and
 *       cache storage.</li>
 * </ul>
 */
@Configuration
public class RedisServerConfiguration {

    @Value("${redis.enable.embedded}")
    private boolean usingEmbeddedRedis;

    @Value("${redis.enable.sentinel}")
    private boolean sentinelEnabled;

    @Value("${redis.host.name}")
    private String redisHost;

    @Value("${redis.host.port}")
    private Integer redisPort;

    @Value("${redis.sentinel.master.name}")
    private String sentinelMasterName;

    @Value("#{'${redis.sentinel.hosts}'.split(',')}")
    private List<String> sentinelHosts;

//    @Bean
//    @Conditional(RedisServerConfigCondition.class)
//    public static EmbeddedRedisServer redisServer() {
//        return new EmbeddedRedisServer();
//    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(redisPoolConfig())
                .and()
                .connectTimeout(Duration.ofMillis(5000))
                .readTimeout(Duration.ofMillis(5000))
                .build();
        if (!usingEmbeddedRedis && sentinelEnabled) {
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
            sentinelConfig.master(sentinelMasterName);
            for (String host : sentinelHosts) {
                String[] hostConfig = host.split(":");
                sentinelConfig.sentinel(hostConfig[0].trim(), Integer.parseInt(hostConfig[1].trim()));
            }
            return new JedisConnectionFactory(sentinelConfig, jedisClientConfiguration);
        } else {
            RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
            return new JedisConnectionFactory(standaloneConfig, jedisClientConfiguration);
        }
    }

    @Bean
    public JedisPoolConfig redisPoolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(1800000);
        jedisPoolConfig.setNumTestsPerEvictionRun(-1);
        jedisPoolConfig.setTestOnReturn(false);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        return jedisPoolConfig;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(final RedisConnectionFactory cf) {
        final RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }
}
