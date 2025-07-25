/**
 * Created on May 30, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.filter;

import java.io.IOException;

import org.egov.finance.inbox.config.filter.CachedBodyHttpServletRequest;
import org.egov.finance.inbox.model.RequestInfo;
import org.egov.finance.inbox.util.ApplicationThreadLocals;
import org.egov.finance.inbox.util.InboxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestLogPreFilter implements Filter {

	private ObjectMapper mapper;

	@Autowired
	public RequestLogPreFilter(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(req);
		String body = wrappedRequest.getCachedBodyAsString();
		log.info("Incoming request: " + req.getMethod() + " " + req.getRequestURI());
		//log.info("Request Body : {}", body);

		try {
			JsonNode root = mapper.readTree(body);
			JsonNode reqInfoNode = root.get(InboxConstants.REQUEST_INFO);
			if (reqInfoNode != null && !reqInfoNode.isNull()) {
				RequestInfo reqInfo = mapper.treeToValue(reqInfoNode, RequestInfo.class);
				String schema = (reqInfo.getTenantId().split(InboxConstants.REQUEST_TENANT_SPLIT_REGEX)[1].isBlank()
						? null
						: reqInfo.getTenantId().split(InboxConstants.REQUEST_TENANT_SPLIT_REGEX)[1]);
				ApplicationThreadLocals.setTenantID(schema);
			}

			JsonNode userIdNode = root.path("RequestInfo").path("userInfo").path("id");
			if (!userIdNode.isMissingNode() && userIdNode.isNumber()) {
				Long userId = userIdNode.asLong();
				ApplicationThreadLocals.setCurrentUserId(userId);
			}
			
			JsonNode userAuthToken = root.path("RequestInfo").path("authToken");
			if (!userAuthToken.isMissingNode()) {
				String token = userIdNode.asText();
				ApplicationThreadLocals.setUserToken(token);
			}


		} catch (Exception e) {
			log.warn("Could not extract RequestInfo from body: {}", e.getMessage());
		}

		chain.doFilter(wrappedRequest, response);
	}

}
