package hexlet.code.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Welcome", description = "General welcome/info endpoints")
public final class WelcomeController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring";
    }

    @GetMapping("/secure")
    public String secure() {
        return "This should require JWT";
    }

    @GetMapping("/error-test")
    public String errorTest() {
        throw new RuntimeException("Test exception for Sentry");
    }
}
