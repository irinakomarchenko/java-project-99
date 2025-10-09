package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class TaskDto {
    private Long id;
    @JsonProperty("name")
    private String title;
    private String content;
    private String status;
    private Long statusId;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @JsonProperty("taskLabelIds")
    private Set<Long> labelIds;
}
