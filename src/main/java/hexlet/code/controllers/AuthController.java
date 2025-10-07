package hexlet.code.controllers;

import hexlet.code.dto.AuthRequest;
import hexlet.code.util.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public final class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody AuthRequest authRequest) {
        var authToken = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()
        );

        authenticationManager.authenticate(authToken);

        return jwtUtils.generateToken(authRequest.getUsername());
    }
}
