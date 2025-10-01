package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TaskMapper {


    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskDto toDto(Task entity);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task toEntity(TaskDto dto);


    void update(TaskDto dto, @MappingTarget Task entity);
}
