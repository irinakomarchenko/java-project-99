package hexlet.code.dto;

import lombok.Data;

import java.util.Set;

@Data
public class TaskDto {
    private Long id;

    private String name;

    private String description;

    private Long statusId;
    private Long assigneeId;

    private Set<Long> labelIds;

}
