package hexlet.code.app.dto;

import lombok.Data;

@Data
public class TaskParamsDto {
    private String titleCont;
    private Long assigneeId;
    private String status;
    private Long labelId;
}
