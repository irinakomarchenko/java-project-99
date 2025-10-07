package hexlet.code.mapper;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TaskMapper {

    @Mapping(target = "status", source = "status.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "labelsToIds")
    TaskDto toDto(Task entity);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Task toEntity(TaskDto dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    void update(TaskDto dto, @MappingTarget Task entity);

    @Named("labelsToIds")
    default Set<Long> mapLabelsToIds(Set<Label> labels) {
        return labels != null ? labels.stream().map(Label::getId).collect(Collectors.toSet()) : null;
    }
}

