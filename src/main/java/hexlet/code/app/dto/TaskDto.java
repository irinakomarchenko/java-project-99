package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDto {
    private Long id;

    @NotBlank
    @Size(min = 1)
    private String name;

    private String description;

    private Long statusId;
    private Long assigneeId;
}
