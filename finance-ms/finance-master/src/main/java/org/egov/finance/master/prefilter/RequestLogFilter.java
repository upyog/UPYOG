package org.egov.finance.master.prefilter;

import java.io.IOException;

import org.egov.finance.master.config.CachedBodyHttpServletRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestLogFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
	        HttpServletRequest req = (HttpServletRequest) request;
	        
	        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(req);
	        String body = wrappedRequest.getCachedBodyAsString();
	        log.info("Incoming request: " + req.getMethod() + " " + req.getRequestURI());
	        log.debug("Request Body :{}",body);
	        chain.doFilter(request, response);
	    }

}
