package com.example.gateway.filters.pre.helpers;

import static com.example.gateway.constants.GatewayConstants.AUTH_BOOLEAN_FLAG_NAME;
import static com.example.gateway.constants.GatewayConstants.OPEN_ENDPOINT_MESSAGE;
import static com.example.gateway.constants.GatewayConstants.REQUEST_INFO_FIELD_NAME_PASCAL_CASE;

import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;

import com.example.gateway.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthPreCheckFilterHelper implements RewriteFunction<Map, Map> {

    public static final String AUTH_TOKEN_RETRIEVE_FAILURE_MESSAGE = "Retrieving of auth token failed";
    public static final String ROUTING_TO_ANONYMOUS_ENDPOINT_MESSAGE = "Routing to anonymous endpoint: {}";
    public static final String ROUTING_TO_PROTECTED_ENDPOINT_RESTRICTED_MESSAGE =
            "Routing to protected endpoint {} restricted - No auth token";
    public static final String UNAUTHORIZED_USER_MESSAGE = "You are not authorized to access this resource";
    public static final String PROCEED_ROUTING_MESSAGE = "Routing to an endpoint: {} - auth provided";
    private List<String> openEndpointsWhitelist;
    private List<String> mixedModeEndpointsWhitelist;
    private ObjectMapper objectMapper;

    private UserUtils userUtils;
    public AuthPreCheckFilterHelper(List<String> openEndpointsWhitelist, List<String> mixedModeEndpointsWhitelist,
                                    ObjectMapper objectMapper, UserUtils userUtils) {

        this.openEndpointsWhitelist = openEndpointsWhitelist;
        this.mixedModeEndpointsWhitelist = mixedModeEndpointsWhitelist;
        this.objectMapper = objectMapper;
    }


    @Override
    public Publisher<Map> apply(ServerWebExchange exchange, Map body) {

        String authToken = null;
        String endPointPath = exchange.getRequest().getPath().value();

        if (openEndpointsWhitelist.contains(endPointPath)) {
            exchange.getAttributes().put(AUTH_BOOLEAN_FLAG_NAME, Boolean.FALSE);
            log.info(OPEN_ENDPOINT_MESSAGE, endPointPath);
            return Mono.just(body);
        }

        // CRITICAL FIX: Extract authToken gracefully, similar to Zuul's approach
        // If extraction fails, authToken remains null and we check mixed-mode whitelist before throwing

        try {
        	RequestInfo requestInfo=new RequestInfo();
        	if(body!=null) {
            requestInfo = objectMapper.convertValue(body.get(REQUEST_INFO_FIELD_NAME_PASCAL_CASE), RequestInfo.class);
            authToken = requestInfo.getAuthToken();}
        	else {            
            HttpHeaders httpHeader=exchange.getRequest().getHeaders();
            authToken=httpHeader.get("Auth-Token").get(0);
           
        	}

        } catch (Exception e) {
            log.error(AUTH_TOKEN_RETRIEVE_FAILURE_MESSAGE, e);
            // Don't throw immediately - check if this is a mixed-mode endpoint first
            authToken = null;
        }

        // Check authorization based on authToken presence
        if (ObjectUtils.isEmpty(authToken)) {
            // CRITICAL FIX: Check mixed-mode whitelist before throwing 401
            // This matches Zuul behavior where mixed-mode endpoints allow anonymous access
        	log.info("Mixed endpoints: "+mixedModeEndpointsWhitelist.toString());
            if (mixedModeEndpointsWhitelist.contains(endPointPath)) {
                log.info(ROUTING_TO_ANONYMOUS_ENDPOINT_MESSAGE, endPointPath);
                exchange.getAttributes().put(AUTH_BOOLEAN_FLAG_NAME, Boolean.FALSE);
//                User systemUser = userUtils.fetchSystemUser(requestInfo.getTenantId(), exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER_NAME));
            } else {
                log.info(ROUTING_TO_PROTECTED_ENDPOINT_RESTRICTED_MESSAGE, endPointPath);
                CustomException customException = new CustomException(UNAUTHORIZED_USER_MESSAGE, UNAUTHORIZED_USER_MESSAGE);
                customException.setCode(HttpStatus.UNAUTHORIZED.toString());
                throw customException;
            }
        } else {
            log.info(PROCEED_ROUTING_MESSAGE, endPointPath);
            exchange.getAttributes().put(AUTH_BOOLEAN_FLAG_NAME, Boolean.TRUE);

        }
        if(body==null)
        	return Mono.empty();
        return Mono.just(body);
    }
}
