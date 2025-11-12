package sam.dev.le.service.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sam.dev.le.repository.user.UnverifiedUserRepository;
import sam.dev.le.repository.user.UserRepository;
import sam.dev.le.repository.enums.UserRole;
import sam.dev.le.repository.dtos.auth.SignInRequest;
import sam.dev.le.repository.dtos.auth.SignUpRequest;
import sam.dev.le.repository.entitys.user.UnverifiedUser;
import sam.dev.le.repository.entitys.user.User;
import sam.dev.le.service.jwt.JWTService;
import sam.dev.le.service.mail.MailService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UnverifiedUserRepository unverifiedUserRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public String singUp(SignUpRequest request) {
        try {
            unverifiedUserRepository.deleteUnverifiedUsersByExpirationBefore(LocalDateTime.now());
            if (
                    request.getUsername().isEmpty() ||
                            request.getEmail().isEmpty() ||
                            request.getPassword().isEmpty() ||
                            request.getUsername().length() < 5 || request.getUsername().length() > 60 ||
                            request.getPassword().length() < 8 || request.getPassword().length() > 100
            ) return "bad request";

            if (
                    userRepository.findUserByUsername(request.getUsername()).isPresent() ||
                            userRepository.findUserByEmail(request.getEmail()).isPresent()
            ) return "user with username: "+request.getUsername()+" or email: "+request.getEmail()+" already exist";

            UnverifiedUser user = new UnverifiedUser();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setExpiration(LocalDateTime.now().plusMinutes(20));
            user.setToken(UUID.randomUUID().toString());

            if (
                    mailService.sendMail(
                            request.getEmail(),
                            "verification",
                            "localhost:8080//unauthorized/verification?id="+user.getToken()
                    )==200
            ) {
                logger.info("token --> {}", user.getToken());
                unverifiedUserRepository.save(user);
                return "unverified user was created";
            }

            return "something went wrong";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String verification(String token) {
        try {
            if (token.isEmpty()) return "bad request";

            Optional<UnverifiedUser> optionalUnverifiedUser = unverifiedUserRepository
                    .findUnverifiedUserByToken(token);
            if (optionalUnverifiedUser.isEmpty()) return "user with this token doesn't exist";

            User user = new User();
            user.setUsername(optionalUnverifiedUser.get().getUsername());
            user.setEmail(optionalUnverifiedUser.get().getEmail());
            user.setPassword(optionalUnverifiedUser.get().getPassword());
            user.setRole(UserRole.USER);

            unverifiedUserRepository.deleteUnverifiedUserByToken(token);
            userRepository.save(user);
            return "user was successfully created";

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String signIn(SignInRequest request) {
        try {
            if (
                    request.getPassword().isEmpty() ||
                            request.getEmail().isEmpty()
            ) return "bad request";

            Optional<User> optionalUser = userRepository.findUserByEmail(request.getEmail());
            if (optionalUser.isEmpty()) return "user with email: "+request.getEmail()+" doesn't exist";

            if (
                    passwordEncoder.matches(request.getPassword(), optionalUser.get().getPassword())
            ) return jwtService.generateToken(optionalUser.get().getId());

            return "bad credentials";

            } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
