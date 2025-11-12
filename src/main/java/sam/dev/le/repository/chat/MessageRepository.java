package sam.dev.le.repository.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sam.dev.le.repository.entitys.chat.Message;
import sam.dev.le.repository.entitys.chat.PrivateChat;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(
            "SELECT m FROM Message m " +
                    "JOIN FETCH m.sender " +
                    "JOIN FETCH m.privateChat " +
                    "WHERE m.privateChat = :privateChat " +
                    "ORDER BY m.id DESC"
    )
    Optional<List<Message>> findLast50MessagesByPrivateChat(
            @Param("privateChat") PrivateChat privateChat,
            Pageable pageable
    );

    @Query(
            "SELECT m FROM Message m " +
                    "JOIN FETCH m.sender " +
                    "JOIN FETCH m.privateChat " +
                    "WHERE m.privateChat = :privateChat " +
                    "AND m.id > :lastMessageId "+
                    "ORDER BY m.id DESC"
    )
    Optional<List<Message>> findLast50ExtraMessagesByPrivateChat(
            @Param("privateChat") PrivateChat privateChat,
            @Param("lastMessageId") Long lastMessageId,
            Pageable pageable
    );

}
