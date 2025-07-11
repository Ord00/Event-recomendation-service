package event.rec.service;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtCore {
    @Value("${event-recommendation-service.app.secret}")
    private String secret;

    @Value("${event-recommendation-service.app.lifetime}")
    private Duration lifetime;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDerailsImpl userDetails =(UserDerailsImpl) authentication.getPrincipal();

        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + lifetime.toMillis());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getSigningKey())
                .compact();
    }
}
