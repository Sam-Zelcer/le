package sam.dev.le.repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sam.dev.le.repository.entitys.chat.PrivateChat;
import sam.dev.le.repository.entitys.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChat, Long> {

    @Query(
            "SELECT pch FROM PrivateChat pch "+
                    "JOIN FETCH pch.firstUser " +
                    "JOIN FETCH pch.secondUser "+
                    "WHERE pch.firstUser=:user OR pch.secondUser=:user"
    )
    Optional<List<PrivateChat>> findUsersPrivateChats(
            @Param("user") User user
    );

    @Query(
            "SELECT pch FROM PrivateChat pch "+
                    "JOIN FETCH pch.firstUser "+
                    "JOIN FETCH pch.secondUser "+
                    "WHERE pch.firstUser=:firstUserId AND pch.secondUser=:secondUserId"
    )
    Optional<PrivateChat> isPrivateChatAlreadyExist(
            @Param("firstUserId") User firstUser,
            @Param("secondUserId") User secondUser
    );
}
