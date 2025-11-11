package sam.dev.le.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sam.dev.le.repository.UserRepository;
import sam.dev.le.repository.entitys.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JWTService {

    private final UserRepository userRepository;

    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.expiration-time}")
    private int expiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isPresent()) return Jwts
                .builder()
                .subject(username)
                .claim("role", optionalUser.get().getRole())
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + expiration)))
                .signWith(getSecretKey())
                .compact();

        return "user with username : " + username + " wasn't found";
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean verifyToken(
            String token,
            UserDetails userDetails
    ) {
        final String username = extractUsername(token);
        final Date expiration = getAllClaims(token).getExpiration();
        boolean isExpired = expiration.after(new Date());

        return (username.equals(userDetails.getUsername()) && isExpired);
    }

}
