package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("taskLabelIds")
    private Set<Long> labelIds;
}
