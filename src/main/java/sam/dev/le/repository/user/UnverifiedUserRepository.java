package sam.dev.le.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sam.dev.le.repository.entitys.user.UnverifiedUser;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UnverifiedUserRepository extends JpaRepository<UnverifiedUser, Long> {

    Optional<UnverifiedUser> findUnverifiedUserByToken(String token);
    void deleteUnverifiedUserByToken(String token);
    void deleteUnverifiedUsersByExpirationBefore(LocalDateTime now);
}
