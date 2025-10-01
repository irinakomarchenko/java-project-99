package hexlet.code.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "task_statuses")
@Getter
@Setter
@NoArgsConstructor
public class TaskStatus implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1)
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Size(min = 1)
    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private LocalDate createdAt = LocalDate.now();
}
