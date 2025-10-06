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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

/**
 * Initializes default data in the database on application startup.
 * <p>
 * Creates an admin user, default labels ("feature", "bug"),
 * and task statuses ("draft", "to_review", "to_be_fixed", "to_publish", "published").
 * </p>
 */
@Configuration
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
     * <p>
     * Not intended for overriding — subclasses should call {@code super.run(args)} if extended.
     * </p>
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
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setFirstName("Hexlet");
            admin.setLastName("Admin");
            userRepository.save(admin);
            System.out.printf("Admin user created: %s%n", adminEmail);
        }
    }

    private void initLabels() {
        createLabelIfNotExists("feature");
        createLabelIfNotExists("bug");
    }

    private void createLabelIfNotExists(String name) {
        if (!labelRepository.existsByName(name)) {
            Label label = new Label();
            label.setName(name);
            labelRepository.save(label);
            System.out.printf("Default label created: %s%n", name);
        }
    }

    private void initTaskStatuses() {

        var statuses = List.of(
                Map.entry("Draft", "draft"),
                Map.entry("ToReview", "to_review"),
                Map.entry("ToBeFixed", "to_be_fixed"),
                Map.entry("ToPublish", "to_publish"),
                Map.entry("Published", "published")
        );

        for (var entry : statuses) {
            String name = entry.getKey();
            String slug = entry.getValue();

            if (!statusRepository.existsBySlug(slug)) {
                TaskStatus status = new TaskStatus();
                status.setName(name);
                status.setSlug(slug);
                statusRepository.save(status);
                System.out.printf("Default status created: %s (%s)%n", name, slug);
            }
        }

        statusRepository.findBySlug("draft").orElseGet(() -> {
            TaskStatus draft = new TaskStatus();
            draft.setSlug("draft");
            draft.setName("Draft");
            return statusRepository.save(draft);
        });
    }
}
