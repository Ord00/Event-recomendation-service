package event.rec.service.filter;

import event.rec.service.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
    public class JwtFilter extends AbstractGatewayFilterFactory<JwtFilter.Config> {

    private final JwtTokenUtils jwtTokenUtils;

    public JwtFilter(JwtTokenUtils jwtTokenUtils) {
        super(Config.class);
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            String username;
            String jwtToken;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwtToken = authHeader.substring(7);
                try {
                    if (!jwtToken.isEmpty()) {
                        username = jwtTokenUtils.getUsername(jwtToken);
                        List<String> roles = jwtTokenUtils.getRoles(jwtToken);

                        exchange.getRequest().mutate()
                                .header("X-User-Name", username)
                                .header("X-User-Roles", String.join(",", roles))
                                .build();

                        return chain.filter(exchange);
                    }
                    log.warn("JWT token is empty or invalid.");
                } catch (ExpiredJwtException e) {
                    log.info("JWT token has expired: {}", jwtToken);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                } catch (MalformedJwtException e) {
                    log.info("JWT token is malformed: {}", jwtToken);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                } catch (Exception e) {
                    log.error("An error occurred while processing JWT token", e);
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                }
            }

            log.warn("Authorization header is missing or does not start with 'Bearer '.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }

    public static class Config {
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.emptyList();
    }

}
