package event.rec.service.filter;

import event.rec.service.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter implements GlobalFilter {
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String username = null;
        String jwtToken;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            try {
                if (!jwtToken.isEmpty()) {
                    username = jwtTokenUtils.getUsername(jwtToken);
                } else {
                    log.warn("JWT token is empty or invalid.");
                }

                List<String> roles = jwtTokenUtils.getRoles(jwtToken);

                exchange.getRequest().mutate()
                        .header("X-User-Name", username)
                        .header("X-User-Roles", String.join(",", roles))
                        .build();

                return chain.filter(exchange);

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
        } else {
            log.warn("Authorization header is missing or does not start with 'Bearer '.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
