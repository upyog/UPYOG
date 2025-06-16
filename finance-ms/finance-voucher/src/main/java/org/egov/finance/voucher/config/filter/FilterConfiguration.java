/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.voucher.config.filter;

import org.egov.finance.voucher.filter.RequestLogPreFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {
	 public FilterRegistrationBean<RequestLogPreFilter> requestLogFilter(){
	        FilterRegistrationBean<RequestLogPreFilter> registrationBean = new FilterRegistrationBean<>();

	        registrationBean.setFilter(new RequestLogPreFilter(null));
	        registrationBean.addUrlPatterns("/*"); // Apply to all URLs
	        registrationBean.setOrder(1); // Set order if multiple filters
	        return registrationBean;
	    }

}
