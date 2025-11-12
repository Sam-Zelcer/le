package sam.dev.le.repository.dtos.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetMessage {

    private String sender;
    private String text;
    private String at;
}
