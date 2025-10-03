package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Initializes default data in the database on application startup.
 * <p>
 * Creates an admin user (if not exists) and inserts default labels ("feature", "bug").
 * This ensures that the system always has initial data to work with.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${app.admin.email:hexlet@example.com}")
    private String adminEmail;

    @Value("${app.admin.password:qwerty}")
    private String adminPassword;

    /**
     * Executes initialization logic on application startup.
     *
     * @param args command-line arguments
     */
    @Override
    public void run(String... args) {
        initAdmin();
        initLabels();
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
        } else {
            System.out.printf("Admin user already exists: %s%n", adminEmail);
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
}
