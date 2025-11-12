package sam.dev.le.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sam.dev.le.repository.dtos.auth.SignInRequest;
import sam.dev.le.repository.dtos.auth.SignUpRequest;
import sam.dev.le.service.user.AuthService;

@RestController
@RequestMapping("/unauthorized")
@AllArgsConstructor
public class UnauthorizedController {

    private final AuthService authService;

    @PostMapping("sign-up")
    public String signUp(
            @RequestBody @Valid
            SignUpRequest request
    ) {
        return authService.singUp(request);
    }

    @GetMapping("/verification")
    public String verification(
            @RequestParam
            String token
    ) {
        return authService.verification(token);
    }

    @PostMapping("sign-in")
    public String signIn(
            @RequestBody @Valid
            SignInRequest request
    ) {
        return authService.signIn(request);
    }
}
