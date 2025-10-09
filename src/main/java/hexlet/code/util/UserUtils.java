package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utility class for authorization checks related to the current authenticated user.
 */
@Component("userUtils")
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    @Value("${app.admin.email:hexlet@example.com}")
    private String adminEmail;

    /**
     * Checks whether the authenticated user has access to the user with the given ID.
     * Access is granted if the user is the same as the authenticated one
     * or if the authenticated user is the configured admin.
     *
     * @param id the target user ID
     * @return true if access is allowed, false otherwise
     */
    public boolean canAccessUser(Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String email = auth.getName();
        if (email.equalsIgnoreCase(adminEmail)) {
            return true;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.getId().equals(id))
                .orElse(false);
    }

    /**
     * Returns the entity of the currently authenticated user.
     *
     * @return the current authenticated user, or null if unauthenticated
     */
    public User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        var email = auth.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
