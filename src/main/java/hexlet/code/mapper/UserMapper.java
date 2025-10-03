package hexlet.code.mapper;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import org.openapitools.jackson.nullable.JsonNullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDto toDto(User model);

    User toEntity(UserDto dto);

    void update(UserDto dto, @MappingTarget User model);

    default String map(JsonNullable<String> value) {
        return value != null && value.isPresent() ? value.get() : null;
    }
}
