package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TaskMapper {


    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "labelsToIds")
    TaskDto toDto(Task entity);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Task toEntity(TaskDto dto);

    void update(TaskDto dto, @MappingTarget Task entity);

    @Named("labelsToIds")
    default Set<Long> mapLabelsToIds(Set<Label> labels) {
        return labels != null ? labels.stream().map(Label::getId).collect(Collectors.toSet()) : null;
    }
}

