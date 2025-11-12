package sam.dev.le.repository.dtos.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetMessagesResponse {

    private List<GetMessage> messages;
    private Long lastMessageId;
}
