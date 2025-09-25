package hexlet.code.app.service;

import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, User newData) {
        User user = getUser(id);

        if (newData.getEmail() != null) user.setEmail(newData.getEmail());
        if (newData.getFirstName() != null) user.setFirstName(newData.getFirstName());
        if (newData.getLastName() != null) user.setLastName(newData.getLastName());
        if (newData.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(newData.getPassword()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
