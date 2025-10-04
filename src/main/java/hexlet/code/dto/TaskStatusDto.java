package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskStatusDto {
    private Long id;

    @Size(min = 1)
    private String name;

    @Size(min = 1)
    private String slug;

    private LocalDateTime createdAt;
}
