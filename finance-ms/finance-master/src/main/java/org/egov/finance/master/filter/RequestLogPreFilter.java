/**
 * Created on May 30, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.egov.finance.master.config.filter.CachedBodyHttpServletRequest;
import org.egov.finance.master.model.RequestInfo;
import org.egov.finance.master.util.ApplicationThreadLocals;
import org.egov.finance.master.util.CommonUtils;
import org.egov.finance.master.util.MasterConstants;
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
	private CommonUtils commonUtils;

	@Autowired
	public RequestLogPreFilter(ObjectMapper mapper,CommonUtils commonUtils) {
		this.mapper = mapper;
		this.commonUtils=commonUtils;
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
			JsonNode reqInfoNode = root.get(MasterConstants.REQUEST_INFO);
			if (reqInfoNode != null && !reqInfoNode.isNull()) {
				RequestInfo reqInfo = mapper.treeToValue(reqInfoNode, RequestInfo.class);
				List<String> masterNames = new ArrayList<>(Arrays.asList("tenants"));
				Map<String, List<String>> codes = commonUtils.getAttributeValues("mn", "tenant", masterNames,
						"[?(@.city.name)].city.districtTenantCode", "$.MdmsRes.tenant");
				List<String> cities = codes.get("tenants");
				cities.add("mn.demo");//as we using demo for testing
				String schema = null;
				if (cities.contains(reqInfo.getTenantId())) {
				    String[] parts = reqInfo.getTenantId().split(MasterConstants.REQUEST_TENANT_SPLIT_REGEX);
				    schema = (parts.length > 1 && !parts[1].isBlank()) ? parts[1] : null;
				}
				ApplicationThreadLocals.setTenantID(schema);
			}

			JsonNode userIdNode = root.path("RequestInfo").path("userInfo").path("id");
			if (!userIdNode.isMissingNode() && userIdNode.isNumber()) {
				Long userId = userIdNode.asLong();
				ApplicationThreadLocals.setCurrentUserId(userId);
			}

		} catch (Exception e) {
			log.warn("Could not extract RequestInfo from body: {}", e.getMessage());
		}

		chain.doFilter(wrappedRequest, response);
	}

}
