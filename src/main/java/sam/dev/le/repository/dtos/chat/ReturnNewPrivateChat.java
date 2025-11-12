package sam.dev.le.repository.dtos.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReturnNewPrivateChat {

    private Long ChatId;
    private String name;
}
