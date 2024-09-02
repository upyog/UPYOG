package org.upyog.chb;


import org.egov.tracer.config.TracerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@Import({
		TracerConfiguration.class /*, EncryptionConfiguration.class */})
@SpringBootApplication
@ComponentScan(basePackages = { "org.upyog.chb"})
//@EnableFeignClients
public class CommunityHallBookingApplication {

	//TODO: is devtools disable required on prod
    public static void main(String[] args) throws Exception {
    	System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(CommunityHallBookingApplication.class, args);
    }
    
	/*
	 * @Bean public RedisTemplate<String, CommunityHallSlotAvailabilityDetail>
	 * redisTemplate(RedisConnectionFactory connectionFactory) {
	 * RedisTemplate<String, CommunityHallSlotAvailabilityDetail> template = new
	 * RedisTemplate<>(); template.setConnectionFactory(connectionFactory);
	 * template.setKeySerializer(new StringRedisSerializer());
	 * template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer()); //
	 * Add some specific configuration here. Key serializers, etc. return template;
	 * }
	 */
    
  

}
