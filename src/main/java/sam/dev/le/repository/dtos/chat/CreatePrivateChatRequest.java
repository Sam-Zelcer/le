package sam.dev.le.repository.dtos.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePrivateChatRequest {

    @NotNull
    private String secondUserNameId;
}
