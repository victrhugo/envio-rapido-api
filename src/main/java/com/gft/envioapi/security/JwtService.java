package com.gft.envioapi.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION = 86400000; // 24h

    private Key getLoginKey() {
        // garanta secret com >= 32 bytes (256 bits) para HS256
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String login, List<String> roles) {
        return Jwts.builder()
                .setSubject(login)
                .claim("roles", roles) // <<< AQUI
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getLoginKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairLogin(String token) {
        return getClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extrairRoles(String token) {
        var claims = getClaims(token);
        Object raw = claims.get("roles");
        if (raw instanceof List<?> list) {
            return (List<String>) list; // esperado: ["ADMIN","USER"]
        }
        // fallback: se usou "role" único ou "scope"
        Object role = claims.get("role");
        if (role instanceof String s && !s.isBlank()) return List.of(s);
        Object scope = claims.get("scope");
        if (scope instanceof String s2 && !s2.isBlank()) return List.of(s2.split("\\s+"));
        return List.of();
    }

    public boolean isTokenValid(String token, String login) {
        final String tokenLogin = extrairLogin(token);
        return (tokenLogin != null && tokenLogin.equals(login) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getLoginKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}