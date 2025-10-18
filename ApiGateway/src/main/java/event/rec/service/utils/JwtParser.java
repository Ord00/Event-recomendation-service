package event.rec.service.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtParser extends JwtBaseParser {
    public String getUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return (List<String>) getClaimsFromToken(token).get("roles");
    }
}
