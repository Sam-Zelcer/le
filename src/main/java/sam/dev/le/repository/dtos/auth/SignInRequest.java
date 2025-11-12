package sam.dev.le.repository.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {

    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
