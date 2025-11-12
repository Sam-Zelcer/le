package sam.dev.le.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import sam.dev.le.repository.user.UserRepository;
import sam.dev.le.repository.entitys.user.User;

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

    public String generateToken(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) return Jwts
                .builder()
                .subject(optionalUser.get().getUsername())
                .claim("userId", userId)
                .claim("role", optionalUser.get().getRole().toString())
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + expiration)))
                .signWith(getSecretKey())
                .compact();

        return "user wasn't found";
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long extractUsersId(String token) {
        return getAllClaims(token).get("userId", Long.class);
    }

    public boolean verifyToken(
            String token,
            UserDetails userDetails
    ) {
        Optional<User> optionalUser = userRepository.findById(extractUsersId(token));
        if (optionalUser.isEmpty()) return false;
        final Date expiration = getAllClaims(token).getExpiration();
        boolean isExpired = expiration.after(new Date());

        return (optionalUser.get().getUsername().equals(userDetails.getUsername()) && isExpired);
    }

}
