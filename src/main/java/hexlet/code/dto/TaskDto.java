package hexlet.code.dto;

import lombok.Data;

import java.util.Set;

@Data
public class TaskDto {
    private Long id;
    private String title;
    private String content;
    private String status;
    private Long statusId;
    private Long assigneeId;
    private Set<Long> labelIds;
}
