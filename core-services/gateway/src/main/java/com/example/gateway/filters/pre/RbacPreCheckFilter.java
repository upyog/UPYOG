package com.example.gateway.filters.pre;

import com.example.gateway.config.ApplicationProperties;
import com.example.gateway.filters.pre.helpers.RbacPreCheckFilterHelper;
import com.example.gateway.filters.pre.helpers.RbacPreCheckFormDataFilterHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.example.gateway.constants.GatewayConstants.*;

@Slf4j
@Component
public class RbacPreCheckFilter implements GlobalFilter, Ordered {

    private ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilter;

    private final List<String> anonymousEndpointsWhitelist;

    private final ApplicationProperties applicationProperties;

    public RbacPreCheckFilter(ModifyRequestBodyGatewayFilterFactory modifyRequestBodyFilter, List<String> anonymousEndpointsWhitelist, ApplicationProperties applicationProperties) {
        this.modifyRequestBodyFilter = modifyRequestBodyFilter;
        this.anonymousEndpointsWhitelist = anonymousEndpointsWhitelist;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String endPointPath = exchange.getRequest().getPath().value();

        // CRITICAL FIX: Check AUTH_BOOLEAN_FLAG_NAME to determine if RBAC should run
        // RBAC requires user enrichment from AuthFilter. If Auth is skipped, RBAC must be skipped too.
        // This fixes mixed-mode endpoints where AUTH=false but RBAC was incorrectly set to true.
        Boolean authFlag = exchange.getAttribute(AUTH_BOOLEAN_FLAG_NAME);

        log.info("RBAC PRE-CHECK: Endpoint: {}, authFlag: {}", endPointPath, authFlag);

        // Skip RBAC for: open endpoints, anonymous endpoints, OR when auth will be skipped
        // Also check mixedModeEndpointsWhitelist to match Zuul behavior
        if(applicationProperties.getOpenEndpointsWhitelist().contains(endPointPath)
            || (anonymousEndpointsWhitelist != null && anonymousEndpointsWhitelist.contains(endPointPath))
            || applicationProperties.getMixedModeEndpointsWhitelist().contains(endPointPath)
            || (authFlag != null && !authFlag)) {  // If Auth is false, RBAC must be false
            exchange.getAttributes().put(RBAC_BOOLEAN_FLAG_NAME, false);
            log.info("RBAC PRE-CHECK: {} - RBAC DISABLED (open/anonymous/mixed-mode or auth disabled)", endPointPath);
        }
        else {
            exchange.getAttributes().put(RBAC_BOOLEAN_FLAG_NAME, true);
            log.info("RBAC PRE-CHECK: {} - RBAC ENABLED (protected endpoint)", endPointPath);
        }
        return  chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return 3;
    }
}
