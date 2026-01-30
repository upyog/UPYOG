package com.example.gateway.filters.pre.helpers;

import com.example.gateway.config.ApplicationProperties;
import com.example.gateway.model.AuthorizationRequest;
import com.example.gateway.model.AuthorizationRequestWrapper;
import com.example.gateway.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.model.CustomException;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.gateway.constants.GatewayConstants.*;

@Slf4j
@Component
public class RbacFilterHelper implements RewriteFunction<Map, Map> {

    private ObjectMapper objectMapper;

    private MultiStateInstanceUtil centralInstanceUtil;

    private CommonUtils commonUtils;

    private ApplicationProperties applicationProperties;

    private RestTemplate restTemplate;

    public RbacFilterHelper(ObjectMapper objectMapper, MultiStateInstanceUtil centralInstanceUtil, CommonUtils commonUtils, ApplicationProperties applicationProperties, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.centralInstanceUtil = centralInstanceUtil;
        this.commonUtils = commonUtils;
        this.applicationProperties = applicationProperties;
        this.restTemplate = restTemplate;
    }

    @Override
    public Publisher<Map> apply(ServerWebExchange serverWebExchange, Map map) {

        isIncomingURIInAuthorizedActionList(serverWebExchange,map);
        return Mono.just(map);
    }

    private void isIncomingURIInAuthorizedActionList(ServerWebExchange exchange, Map map) {

        String requestUri = exchange.getRequest().getURI().getPath();
        RequestInfo requestInfo = objectMapper.convertValue(map.get(REQUEST_INFO_FIELD_NAME_PASCAL_CASE), RequestInfo.class);
        User user = requestInfo.getUserInfo();

        if (user == null) {
            throw new RuntimeException("User information not found. Can't execute RBAC filter");
        }

        Set<String> tenantIds = commonUtils.validateRequestAndSetRequestTenantId(exchange,map);

        /*
         * Adding tenantId to header for tracer logging with correlation-id
         */
        if (centralInstanceUtil.getIsEnvironmentCentralInstance() && StringUtils.isEmpty(exchange.getAttributes().get(TENANTID_MDC))) {
            String singleTenantId = commonUtils.getLowLevelTenantIdFromSet(tenantIds);
            MDC.put(TENANTID_MDC, singleTenantId);
            exchange.getAttributes().put(TENANTID_MDC, singleTenantId);
        }

        exchange.getAttributes().put(CURRENT_REQUEST_TENANTID, String.join(",", tenantIds));

        // Log roles being sent for authorization
        log.info("RBAC CHECK: User {} has {} roles for URI {}",
                user.getUuid(), user.getRoles() != null ? user.getRoles().size() : 0, requestUri);
        if (user.getRoles() != null) {
            user.getRoles().forEach(role ->
                log.info("  RBAC CHECK: Role - code: {}, name: {}, tenantId: {}",
                        role.getCode(), role.getName(), role.getTenantId())
            );
        }

        AuthorizationRequest request = AuthorizationRequest.builder()
                .roles(new HashSet<>(user.getRoles()))
                .uri(requestUri)
                .tenantIds(tenantIds)
                .build();

        log.info("RBAC CHECK: Calling access-control service at {} with {} roles for URI {}",
                applicationProperties.getAuthorizationUrl(), request.getRoles().size(), requestUri);

        boolean isUriAuthorised = isUriAuthorized(request, requestInfo, exchange);

        log.info("RBAC CHECK: Authorization result for URI {} : {}", requestUri, isUriAuthorised);


        if(!isUriAuthorised) {
            throw new CustomException(HttpStatus.UNAUTHORIZED.toString(), "You are not authorized to access this resource");
        }

    }

    private boolean isUriAuthorized(AuthorizationRequest authorizationRequest, RequestInfo requestInfo, ServerWebExchange exchange) {

        // Use the RequestInfo from the incoming request which includes authToken for authentication
        // This ensures external access-control services can authenticate the request
        AuthorizationRequestWrapper authorizationRequestWrapper = new AuthorizationRequestWrapper(requestInfo, authorizationRequest);


        final HttpHeaders headers = new HttpHeaders();

        headers.add(CORRELATION_ID_HEADER_NAME, (String) exchange.getAttributes().get(CORRELATION_ID_KEY));

        if (centralInstanceUtil.getIsEnvironmentCentralInstance())
            headers.add(REQUEST_TENANT_ID_KEY, (String) exchange.getAttributes().get(TENANTID_MDC));

        final HttpEntity<Object> httpEntity = new HttpEntity<>(authorizationRequestWrapper, headers);

        try {

            ResponseEntity<Void> responseEntity = restTemplate.postForEntity(applicationProperties.getAuthorizationUrl(), httpEntity, Void
                    .class);

            log.info("RBAC CHECK: Access-control service returned status: {}", responseEntity.getStatusCode());
            return responseEntity.getStatusCode().equals(HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            log.warn("RBAC CHECK: Access-control returned error status: {} for URI: {} with {} roles",
                    e.getStatusCode(), authorizationRequest.getUri(), authorizationRequest.getRoles().size());
            log.warn("Exception while attempting to authorize via access control", e);
            return false;
        } catch (Exception e) {
            log.error("RBAC CHECK: Unknown exception occurred while calling access-control for URI: {}",
                    authorizationRequest.getUri(), e);

            return false;
        }

    }
}
