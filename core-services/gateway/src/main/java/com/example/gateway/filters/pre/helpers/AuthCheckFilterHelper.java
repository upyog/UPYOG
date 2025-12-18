package com.example.gateway.filters.pre.helpers;

import static com.example.gateway.constants.GatewayConstants.REQUEST_INFO_FIELD_NAME_PASCAL_CASE;

import java.util.HashMap;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.gateway.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthCheckFilterHelper implements RewriteFunction<Map, Map> {

    private ObjectMapper objectMapper;

    private UserUtils userUtils;

    public AuthCheckFilterHelper(ObjectMapper objectMapper, UserUtils userUtils) {
        this.objectMapper = objectMapper;
        this.userUtils = userUtils;
    }

    @Override
    public Publisher<Map> apply(ServerWebExchange serverWebExchange, Map body) {
    	String authToken=null;
    	 RequestInfo requestInfo= new RequestInfo();
    	
        try {
        	if(body!=null)
        	{
           
            requestInfo = objectMapper.convertValue(body.get(REQUEST_INFO_FIELD_NAME_PASCAL_CASE), RequestInfo.class);
            authToken = requestInfo.getAuthToken();
        	}
        	else
        	{
        		HttpHeaders httpHeader=serverWebExchange.getRequest().getHeaders();
                authToken=httpHeader.get("Auth-Token").get(0);
        	}
            User user = userUtils.getUser(authToken);

            // Log user info received from /_details endpoint
            log.info("AUTH CHECK: Received user from /_details - UUID: {}, userName: {}, roles: {}",
                    user.getUuid(), user.getUserName(), user.getRoles() != null ? user.getRoles().size() : 0);
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                user.getRoles().forEach(role ->
                    log.info("  AUTH CHECK: User role - code: {}, name: {}, tenantId: {}",
                            role.getCode(), role.getName(), role.getTenantId())
                );
            } else {
                log.warn("AUTH CHECK: WARNING - User {} has NO ROLES! This will cause RBAC failures!", user.getUuid());
            }

            requestInfo.setUserInfo(user);
            
            if(body==null) {
            	body= new HashMap<>();
            }

            body.put(REQUEST_INFO_FIELD_NAME_PASCAL_CASE, requestInfo);
            return Mono.just(body);
        } catch (Exception ex) {
            log.error("An error occured while transforming the request body in class RequestBodyRewrite. {}", ex);

            // Throw a custom exception
            throw new CustomException("AUTHENTICATION_ERROR", ex.getMessage());
        }
    }

}
