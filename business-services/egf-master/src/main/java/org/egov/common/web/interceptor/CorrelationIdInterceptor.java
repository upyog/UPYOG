package org.egov.common.web.interceptor;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.egov.common.web.contract.RequestContext;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

public class CorrelationIdInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		final String correlationId = getCorrelationId(request);
		MDC.put(RequestContext.CORRELATION_ID, correlationId);
		RequestContext.setId(correlationId);
		return true;
	}

	private String getCorrelationId(HttpServletRequest request) {
		final String incomingCorrelationId = request.getHeader(RequestContext.CORRELATION_ID);
		return incomingCorrelationId == null ? UUID.randomUUID().toString() : incomingCorrelationId;
	}
}
