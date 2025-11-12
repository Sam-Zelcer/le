package sam.dev.le.repository.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank
    @Size(min = 5, max = 60)
    private String username;

    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
