package hr.iisbackend.service;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class JwtService {
    private static final String SECRET_KEY = "password";
    private static final long REFRESH_EXPIRATION = 1000 * 60L * 60L * 24L;
    private static final long ACCESS_EXPIRATION = 1000 * 60L;

    public String generateAccessToken(String username) {
        return buildToken(username, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, REFRESH_EXPIRATION);
    }

    private String buildToken(String subject, long expirationMs) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}

