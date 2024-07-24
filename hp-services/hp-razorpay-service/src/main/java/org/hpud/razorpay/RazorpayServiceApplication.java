package org.hpud.razorpay;

import org.hpud.razorpay.contract.ResponseInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class RazorpayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RazorpayServiceApplication.class, args);
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	@Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
	 @Bean
	    public ResponseInfo responseInfo() {
	        return new ResponseInfo();
	    }

}
