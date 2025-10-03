package hexlet.code.app.controllers;

import hexlet.code.app.dto.AuthRequest;
import hexlet.code.app.util.JWTUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication & Authorization")
public class AuthController {

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
