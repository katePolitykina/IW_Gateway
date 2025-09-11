package org.example.iw_payment_gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtToHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    if (securityContext != null && securityContext.getAuthentication() instanceof JwtAuthenticationToken jwtAuth) {
                        Jwt jwt = jwtAuth.getToken(); // достаем Jwt
                        String userServiceId = jwt.getClaimAsString("userServiceId");
                        List<String> roles = jwt.getClaimAsStringList("roles");

                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(r -> r.headers(headers -> {
                                    if (userServiceId != null) {
                                        headers.add("X-User-Id", userServiceId);
                                    }
                                    if (roles != null && !roles.isEmpty()) {
                                        headers.add("X-Roles", String.join(",", roles));
                                    }
                                    headers.remove(HttpHeaders.AUTHORIZATION);
                                }))
                                .build();

                        return chain.filter(mutatedExchange);
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));

    }




    @Override
    public int getOrder() {
        return -1;
    }
}
