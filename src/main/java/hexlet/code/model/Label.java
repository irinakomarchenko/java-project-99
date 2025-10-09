package hexlet.code.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a label used for task categorization.
 * The class is final to prevent unsafe inheritance that could
 * break equals/hashCode or JPA proxy behavior.
 */
@Entity
@Table(name = "labels")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Label implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 1000)
    @Column(nullable = false, unique = true)
    private String name;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDate createdAt;

    @ManyToMany(mappedBy = "labels")
    private Set<Task> tasks = new HashSet<>();

    /**
     * Compares this label with another object for equality.
     * Equality is based on {@code id} if both are non-null, otherwise on {@code name}.
     *
     * @param o the object to compare with
     * @return true if the objects represent the same label
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Label)) {
            return false;
        }
        Label label = (Label) o;
        if (id != null && label.id != null) {
            return Objects.equals(id, label.id);
        }
        return Objects.equals(name, label.name);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     * Uses {@code id} if available, otherwise {@code name}.
     *
     * @return hash code for this label
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(name);
    }
}
