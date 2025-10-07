package hexlet.code.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * Called when an unauthenticated user tries to access a protected resource.
     * <p>
     * Subclasses may override this method to provide custom JSON response
     * instead of default HTTP 401 error.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String uri = request.getRequestURI();
        if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "status": 401,
              "error": "Unauthorized",
              "message": "JWT token is missing or invalid"
            }
            """);
    }
}
