package hexlet.code.mapper;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    /**
     * Converts UserDto to User entity with encoded password.
     *
     * @param dto UserDto object
     * @return User entity
     */
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(dto.getPassword()))")
    public abstract User toEntity(UserDto dto);

    /**
     * Converts User entity to UserDto.
     *
     * @param entity User entity
     * @return UserDto object
     */
    public abstract UserDto toDto(User entity);

    @Mapping(target = "password", ignore = true)
    public abstract void update(UserDto dto, @MappingTarget User entity);

    /**
     * Returns encoded password if provided, otherwise keeps the existing one.
     *
     * @param dto UserDto object
     * @param entity existing User entity
     * @return encoded or existing password
     */
    protected final String updatePassword(UserDto dto, User entity) {
        if (dto.getPassword() == null) {
            return entity.getPassword();
        }
        return passwordEncoder.encode(dto.getPassword());
    }
}
