package org.egov.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.egov.constants.RequestContextConstants.AUTH_TOKEN_KEY;

/**
 * 5th pre filter to get executed.
 * Converts authentication token from cookie to header for internal microservice communication.
 * This ensures internal services can use standard header-based authentication
 * while browsers use secure HttpOnly cookies.
 */
@Component
public class CookieToHeaderFilter extends ZuulFilter {

    private static final String INTERNAL_AUTH_HEADER = "x-auth-token";
    private static final String ADDED_INTERNAL_AUTH_HEADER_MESSAGE = "Added internal auth header for downstream service";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 4; // Execute after AuthFilter (order 3)
    }

    @Override
    public boolean shouldFilter() {
        // Always run to ensure internal services get the token in header format
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String authToken = (String) ctx.get(AUTH_TOKEN_KEY);

        // If auth token exists (from any source: cookie, header, or body)
        // Add it to request header for downstream services
        if (authToken != null && !authToken.isEmpty()) {
            // Add token to Zuul request headers for internal microservices
            ctx.addZuulRequestHeader(INTERNAL_AUTH_HEADER, authToken);

            // Also add to standard Authorization header for services expecting Bearer tokens
            ctx.addZuulRequestHeader("Authorization", "Bearer " + authToken);

            logger.debug(ADDED_INTERNAL_AUTH_HEADER_MESSAGE + " for URI: {}",
                ctx.getRequest().getRequestURI());
        }

        return null;
    }
}
