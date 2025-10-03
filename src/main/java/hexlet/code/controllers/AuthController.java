package hexlet.code.controllers;

import hexlet.code.dto.AuthRequest;
import hexlet.code.util.JWTUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication & Authorization")
public final class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()
        );
        authenticationManager.authenticate(authToken);

        return jwtUtils.generateToken(authRequest.getUsername());
    }
}
