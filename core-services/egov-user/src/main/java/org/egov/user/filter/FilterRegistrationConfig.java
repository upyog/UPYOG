package org.egov.user.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterRegistrationConfig {
	
	
	//@Bean
    public FilterRegistrationBean  customFilterRegistrationBean() {
        FilterRegistrationBean captchaFilter = new FilterRegistrationBean();

        // Set the filter instance
       // captchaFilter.setFilter(new CaptchaFilter());
	  //  captchaFilter.setFilter(new CaptchaFilter());
	   // captchaFilter.addUrlPatterns("/oauth/token");
	    //captchaFilter.setOrder(2);
	        
	    return captchaFilter;    
	}
}
