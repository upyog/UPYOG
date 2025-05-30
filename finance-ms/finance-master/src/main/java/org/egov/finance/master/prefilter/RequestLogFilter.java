package org.egov.finance.master.prefilter;

import java.io.IOException;

import org.egov.finance.master.config.Filter.CachedBodyHttpServletRequest;
import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;

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
	@Autowired
	private ObjectMapper mapper;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	        throws IOException, ServletException {
	        HttpServletRequest req = (HttpServletRequest) request;
	        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(req);
	        String body = wrappedRequest.getCachedBodyAsString();
	        log.info("Incoming request: " + req.getMethod() + " " + req.getRequestURI());
	        log.debug("Request Body : {}", body);

	        try {
	            JsonNode root = mapper.readTree(body);
	            JsonNode reqInfoNode = root.get("RequestInfo");
	            if (reqInfoNode != null && !reqInfoNode.isNull()) {
	                RequestInfo reqInfo = mapper.treeToValue(reqInfoNode, RequestInfo.class);
	                // Now set the tenant ID to thread-local or context
	              //  System.out.println(reqInfo.getTenantId().split("\\.")[0]);
	                
	                String schema = (reqInfo.getTenantId().split("\\.")[1].isBlank()?null:reqInfo.getTenantId().split("\\.")[1]);
	                ApplicationThreadLocals.setTenantID(schema);
	            }
	        } catch (Exception e) {
	            log.warn("Could not extract RequestInfo from body: {}", e.getMessage());
	        }
	        
	        chain.doFilter(wrappedRequest, response);
	    }

}
