package org.egov.user.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class CaptchaFilter {
	//@Override
    public void doFilter(
      ServletRequest request, 
      ServletResponse response, 
      FilterChain chain) throws ServletException, IOException {
 
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println(req.getParameterMap());
        System.out.println("Starting a transaction for req : {}"+ req.getRequestURI());
 
        chain.doFilter(request, response);
        System.out.println("Committing a transaction for req : {}"+ req.getRequestURI());
    }

	//@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}


}
