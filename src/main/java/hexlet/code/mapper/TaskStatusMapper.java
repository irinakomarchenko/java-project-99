package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TaskStatusMapper {

    TaskStatusDto toDto(TaskStatus model);

    TaskStatus toEntity(TaskStatusDto dto);

    void update(TaskStatusDto dto, @MappingTarget TaskStatus model);
}
