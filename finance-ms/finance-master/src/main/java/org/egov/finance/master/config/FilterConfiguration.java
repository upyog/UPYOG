package org.egov.finance.master.config;

import org.egov.finance.master.prefilter.RequestLogFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {
	 public FilterRegistrationBean<RequestLogFilter> loggingFilter(){
	        FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();

	        registrationBean.setFilter(new RequestLogFilter());
	        registrationBean.addUrlPatterns("/*"); // Apply to all URLs
	        registrationBean.setOrder(1); // Set order if multiple filters

	        return registrationBean;
	    }

}
