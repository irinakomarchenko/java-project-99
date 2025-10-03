package hexlet.code.mapper;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LabelMapper {

    LabelDto toDto(Label entity);

    Label toEntity(LabelDto dto);

    void update(LabelDto dto, @MappingTarget Label entity);
}
