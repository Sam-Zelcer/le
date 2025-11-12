package sam.dev.le.repository.dtos.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetMessagesRequest {

    @NotNull
    private Long chatId;

    private Long lastMessageId;
}
