package org.egov.filters.post;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.egov.constants.RequestContextConstants.CORRELATION_ID_KEY;

/**
 * Sets the correlation id to the response header.
 */
@Component
public class ResponseEnhancementFilter extends ZuulFilter {

    private static final String CONTENT_SECURITY_POLICY = "Content-Security-Policy";
	private static final String X_XSS_PROTECTION = "x-xss-protection";
	private static final String X_FRAME_OPTIONS = "x-frame-options";
	private static final String X_CONTENT_TYPE_OPTIONS = "x-content-type-options";
	private static final String EXPIRES = "expires";
	private static final String STRICT_TRANSPORT_SECURITY = "strict-transport-security";
	private static final String REFERRER_POLICY = "Referrer-Policy";
	private static final String PRAGMA = "pragma";
	private static final String CACHE_CONTROL = "Cache-Control";
	private static final String CORRELATION_HEADER_NAME = "x-correlation-id";
    private static final String RECEIVED_RESPONSE_MESSAGE = "Received response code: {} from upstream URI {}";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulResponseHeader(CORRELATION_HEADER_NAME, getCorrelationId());
        ctx.addZuulResponseHeader(CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
        ctx.addZuulResponseHeader(PRAGMA,"no-cache");
        ctx.addZuulResponseHeader(REFERRER_POLICY,"strict-origin-when-cross-origin");
        ctx.addZuulResponseHeader(STRICT_TRANSPORT_SECURITY,"max-age=15724800; includeSubDomains");
        ctx.addZuulResponseHeader(EXPIRES,"0");
        ctx.addZuulResponseHeader(X_CONTENT_TYPE_OPTIONS,"nosniff");
        ctx.addZuulResponseHeader(X_FRAME_OPTIONS,"DENY");
        ctx.addZuulResponseHeader(X_XSS_PROTECTION,"1; mode=block");
        ctx.addZuulResponseHeader(CONTENT_SECURITY_POLICY, "default-src 'self'");

        return null;
    }

    private String getCorrelationId() {
        RequestContext ctx = RequestContext.getCurrentContext();
        logger.info(RECEIVED_RESPONSE_MESSAGE,
            ctx.getResponse().getStatus(), ctx.getRequest().getRequestURI());
        return (String) ctx.get(CORRELATION_ID_KEY);
    }
}
