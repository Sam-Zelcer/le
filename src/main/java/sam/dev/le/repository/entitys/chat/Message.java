package sam.dev.le.repository.entitys.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sam.dev.le.repository.entitys.user.User;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message_table")
public class Message {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "message_table_id_seq"
    )
    @SequenceGenerator(
            name = "message_table_id_seq",
            sequenceName = "message_table_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne()
    @JoinColumn(name = "sender", nullable = false)
    private User sender;

    @ManyToOne()
    @JoinColumn(name = "chat", nullable = false)
    private PrivateChat privateChat;

    @Column(name = "at", nullable = false)
    private String at;
}
