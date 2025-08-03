package event.rec.service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;

@Slf4j
@Component
public class JwtBaseParser {
    @Value("${jwt.secret}")
    protected String secret;

    @Value("${jwt.lifetime}")
    protected Duration lifetime;

    protected SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    protected Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
