package hexlet.code.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1)
    private String name;

    private String description;


    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private TaskStatus status;


    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDate createdAt;
}
