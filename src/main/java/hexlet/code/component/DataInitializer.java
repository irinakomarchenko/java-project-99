package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

/**
 * Initializes default data in the database on application startup.
 * <p>
 * Creates:
 * <ul>
 *   <li>Admin user (if not exists)</li>
 *   <li>Default labels ("feature", "bug")</li>
 *   <li>Default task statuses ("draft", "to_review", "to_be_fixed", "to_publish", "published")</li>
 * </ul>
 * </p>
 */
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TaskStatusRepository statusRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${app.admin.email:hexlet@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:qwerty}")
    private String adminPassword;

    /**
     * Called automatically by Spring Boot at startup.
     * Initializes default user, labels and task statuses.
     *
     * @param args command-line arguments
     */
    @Override
    public void run(String... args) {
        initAdmin();
        initLabels();
        initTaskStatuses();
    }

    private void initAdmin() {
        userRepository.findByEmail(adminEmail).orElseGet(() -> {
            var admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setFirstName("Hexlet");
            admin.setLastName("Admin");
            userRepository.save(admin);
            System.out.printf("Admin user created: %s%n", adminEmail);
            return admin;
        });
    }

    private void initLabels() {
        for (String name : List.of("feature", "bug")) {
            if (!labelRepository.existsByName(name)) {
                var label = new Label();
                label.setName(name);
                labelRepository.save(label);
                System.out.printf("Default label created: %s%n", name);
            }
        }
    }

    private void initTaskStatuses() {
        var statuses = List.of(
                Map.entry("Draft", "draft"),
                Map.entry("To review", "to_review"),
                Map.entry("To be fixed", "to_be_fixed"),
                Map.entry("To publish", "to_publish"),
                Map.entry("Published", "published")
        );

        for (var entry : statuses) {
            var name = entry.getKey();
            var slug = entry.getValue();

            if (!statusRepository.existsBySlug(slug)) {
                var status = new TaskStatus();
                status.setName(name);
                status.setSlug(slug);
                statusRepository.save(status);
                System.out.printf("Default status created: %s (%s)%n", name, slug);
            }
        }
    }
}
