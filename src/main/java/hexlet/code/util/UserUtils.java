package hexlet.code.util;

import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper class for working with the currently authenticated user.
 * Used in {@code @PreAuthorize} annotations to check access rights.
 */
@Component("userUtils")
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;

    /**
     * Checks whether the currently authenticated user matches the provided identifier.
     *
     * @param id the identifier of the user to check
     * @return {@code true} if the current user matches the given identifier,
     *         {@code false} otherwise
     */
    public boolean isCurrentUser(Long id) {
        var user = getCurrentUser();
        return user != null && user.getId().equals(id);
    }

    /**
     * Returns the currently authenticated user entity.
     *
     * @return a {@link User} representing the current authenticated user,
     *         or {@code null} if the user is not authenticated
     */
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        var email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
