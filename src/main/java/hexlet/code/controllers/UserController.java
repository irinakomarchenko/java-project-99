package hexlet.code.controllers;

import hexlet.code.dto.UserDto;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

/**
 * REST controller for managing users.
 * Provides CRUD operations with access control.
 * Registration is open, while editing and deleting require authorization.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;

    /**
     * Returns all users (authentication required).
     *
     * @return list of users with total count header
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    /**
     * Returns a user by ID (authentication required).
     *
     * @param id user ID
     * @return user with the specified ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    /**
     * Creates a new user (registration, open access).
     *
     * @param dto user data
     * @return created user with location header
     */
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto) {
        UserDto created = userService.createUser(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates a user (allowed for ADMIN or the user themselves).
     *
     * @param id  user ID
     * @param dto updated user data
     * @return updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userUtils.isCurrentUser(#id)")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    /**
     * Deletes a user (allowed for ADMIN or the user themselves).
     *
     * @param id user ID
     * @return empty response with HTTP 204
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userUtils.isCurrentUser(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
