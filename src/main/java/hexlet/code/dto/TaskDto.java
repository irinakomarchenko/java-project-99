package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class TaskDto {
    private Long id;

    @NotBlank
    @Size(min = 1)
    private String name;

    private String description;

    private Long statusId;
    private Long assigneeId;

    private Set<Long> labelIds;

}
