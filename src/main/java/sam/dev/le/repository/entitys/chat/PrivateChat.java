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
@Table(name = "private_chat_table")
public class PrivateChat {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "private_chat_table_id_seq"
    )
    @SequenceGenerator(
            name = "private_chat_table_id_seq",
            sequenceName = "private_chat_table_id_seq",
            allocationSize = 1
    )
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "first_user", nullable = false)
    private User firstUser;

    @ManyToOne()
    @JoinColumn(name = "second_user", nullable = false)
    private User secondUser;
}
