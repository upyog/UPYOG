package org.egov.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.JedisShardInfo;


public class Redisconfig_OLD {
    
    @Value("${spring.redis.host}")
    private String host;
    
    
    public JedisConnectionFactory connectionFactory() {
        return new JedisConnectionFactory(new JedisShardInfo(host));
    }

    
    public RedisTemplate<String, Long> redisTemplate() {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        return template;
    }

}
