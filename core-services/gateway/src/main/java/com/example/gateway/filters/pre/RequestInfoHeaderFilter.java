package com.example.gateway.filters.pre;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestInfoHeaderFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        HttpHeaders headers = exchange.getRequest().getHeaders();

        // If already present, skip
        if (headers.containsKey("X-Request-Info")) {
            return chain.filter(exchange);
        }

        String tenantId = exchange.getRequest()
                .getQueryParams()
                .getFirst("tenantId");

        User user = null;

        if (tenantId != null) {
            user = new User();
            user.setTenantId(tenantId);
        }

        RequestInfo requestInfo = RequestInfo.builder()
                .apiId("gateway")
                .ver("1.0")
                .userInfo(user)   // tenantId lives here
                .build();

        try {
            String requestInfoJson = objectMapper.writeValueAsString(requestInfo);

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(r -> r.header("X-Request-Info", requestInfoJson))
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            log.error("Failed to inject RequestInfo header", e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
