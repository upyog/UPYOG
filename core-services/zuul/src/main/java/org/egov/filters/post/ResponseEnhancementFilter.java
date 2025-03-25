package org.egov.filters.post;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.egov.constants.RequestContextConstants.CORRELATION_ID_KEY;
import java.security.SecureRandom;
import java.util.Base64;

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
    private static final String PERMISSIONS_POLICY="Permissions-Policy";
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
        
      //  ctx.addZuulResponseHeader(CONTENT_SECURITY_POLICY, "default-src 'self'");
       final String fontgoogleApiUrl= "https://fonts.googleapis.com; ";
       final String fontstaticUrl= " https://fonts.gstatic.com data:; ";
       final String teraformUrl= "https://mnptapp-terraform.s3.ap-south-1.amazonaws.com; ";
       
        ctx.addZuulResponseHeader(PERMISSIONS_POLICY, "geolocation=(self)");
        final SecureRandom SECURE_RANDOM = new SecureRandom();
        byte[] nonceBytes = new byte[16];
        SECURE_RANDOM.nextBytes(nonceBytes);
        String nonce = Base64.getEncoder().encodeToString(nonceBytes);
 
        // Set CSP header dynamically with nonce
       /* ctx.addZuulResponseHeader(CONTENT_SECURITY_POLICY,
                "default-src 'self'; " +
                "script-src 'self' ' nonce-" + nonce + "'; " +
                "style-src 'self' ' nonce-" + nonce + "'"+ fontgoogleApiUrl+
                "font-src 'self'"+ fontstaticUrl+
                "img-src 'self' "+ teraformUrl+
                "frame-ancestors 'none';");*/
        
        ctx.addZuulResponseHeader(CONTENT_SECURITY_POLICY,
                "default-src 'self'; " +
                "script-src 'self' 'nonce-" + nonce + "'; " +
                "style-src 'self' 'nonce-" + nonce + "' "+ fontgoogleApiUrl+
                "font-src 'self'"+ fontstaticUrl+
                "img-src 'self' "+ teraformUrl+
                "frame-ancestors 'none';");
      
    	
      //  default-src 'self'; script-src 'self' 'nonce-sVaHuG7g5ay+Q1QWsrxmtg=='; style-src 'self' ' nonce-sVaHuG7g5ay+Q1QWsrxmtg=='https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com data:; img-src 'self' https://mnptapp-terraform.s3.ap-south-1.amazonaws.com; frame-ancestors 'none';
        
      //  default-src 'self'; script-src 'self' 'nonce-UauTkwu4Vz9x9Eq0UDKaDw==' https://s3.ap-south-1.amazonaws.com; style-src 'self' 'nonce-UauTkwu4Vz9x9Eq0UDKaDw==' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com data:; img-src 'self' https://mnptapp-terraform.s3.ap-south-1.amazonaws.com; frame-ancestors 'none';
        ctx.addZuulRequestHeader ("cspNonce", nonce);
        ctx.getRequest().setAttribute("cspNonce", nonce);
        
        
        return null;
    }

    private String getCorrelationId() {
        RequestContext ctx = RequestContext.getCurrentContext();
        logger.info(RECEIVED_RESPONSE_MESSAGE,
            ctx.getResponse().getStatus(), ctx.getRequest().getRequestURI());
        return (String) ctx.get(CORRELATION_ID_KEY);
    }
}
