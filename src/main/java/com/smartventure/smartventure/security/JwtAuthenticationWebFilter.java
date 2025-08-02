package com.smartventure.smartventure.security;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ReactiveUserDetailsService uds;

    public JwtAuthenticationWebFilter(JwtUtil jwtUtil,
                                      ReactiveUserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.uds = uds;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    return uds.findByUsername(username)
                            .flatMap(user -> {
                                Authentication auth = new UsernamePasswordAuthenticationToken(
                                        user, null, user.getAuthorities()
                                );
                                return chain.filter(exchange)
                                        .contextWrite(
                                                ReactiveSecurityContextHolder.withAuthentication(auth)
                                        );
                            });
                }
            } catch (JwtException ignore) {
                // invalid token — просто пускаем дальше как анонимус
            }
        }
        return chain.filter(exchange);
    }
}
