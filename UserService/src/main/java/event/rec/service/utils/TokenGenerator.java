package event.rec.service.utils;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TokenGenerator extends JwtBaseParser {

    public String generateToken(UserDetails userDetails) {
        if (lifetime == null) {
            throw new IllegalStateException("JWT lifetime is not configured");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList());

        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + lifetime.toMillis());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getSigningKey())
                .compact();
        log.info("Generated token for user: {}", userDetails.getUsername());
        return token;
    }
}
