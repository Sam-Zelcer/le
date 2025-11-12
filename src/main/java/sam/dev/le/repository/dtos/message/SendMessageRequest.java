package sam.dev.le.repository.dtos.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotNull
    private Long chatId;

    @NotBlank
    @Size(max = 1000)
    private String text;
}
