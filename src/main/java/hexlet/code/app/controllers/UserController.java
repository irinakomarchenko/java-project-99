package hexlet.code.app.controllers;

import hexlet.code.app.dto.UserDto;
import hexlet.code.app.model.User;
import hexlet.code.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserDto::fromEntity)
                .collect(toList());
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return UserDto.fromEntity(userService.getUser(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return ResponseEntity.ok(UserDto.fromEntity(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ResponseEntity.ok(UserDto.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
