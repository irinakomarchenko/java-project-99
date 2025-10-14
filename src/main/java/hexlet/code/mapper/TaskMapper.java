package hexlet.code.mapper;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskMapper {

    @Autowired
    protected TaskStatusRepository statusRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected LabelRepository labelRepository;

    @Value("${app.default-status:draft}")
    private String defaultStatusSlug;

    @Mapping(target = "status", source = "status.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "labelsToIds")
    public abstract TaskDto toDto(Task entity);

    @Mapping(target = "status", source = ".", qualifiedByName = "statusFromDto")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "userFromId")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "labelsFromIds")
    public abstract Task toEntity(TaskDto dto);

    @Mapping(target = "status", source = ".", qualifiedByName = "statusFromDto")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "userFromId")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "labelsFromIds")
    public abstract void update(TaskDto dto, @MappingTarget Task entity);

    /**
     * Converts a set of Label entities to a set of their IDs.
     * <p>
     * This helper method is final and should not be overridden.
     *
     * @param labels Set of Label entities
     * @return Set of label IDs or an empty Set if null
     */
    @Named("labelsToIds")
    public Set<Long> mapLabelsToIds(Set<Label> labels) {
        return labels == null ? Set.of()
                : labels.stream().map(Label::getId).collect(Collectors.toSet());
    }

    /**
     * Converts a set of label IDs to a set of Label entities.
     * This helper method is final and should not be overridden.
     *
     * @param labelIds Set of label IDs
     * @return Set of Label entities
     */
    @Named("labelsFromIds")
    public Set<Label> mapLabelsFromIds(Set<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return Set.of();
        }
        Set<Label> foundLabels = labelRepository.findByIdIn(labelIds);
        if (foundLabels.size() != labelIds.size()) {
            var foundIds = foundLabels.stream().map(Label::getId).collect(Collectors.toSet());
            var missingIds = labelIds.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toSet());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Labels not found for ids: " + missingIds);
        }
        return foundLabels;
    }

    /**
     * Converts an assignee ID to a User entity.
     * This helper method is final and should not be overridden.
     *
     * @param id User ID
     * @return User entity or null if id is null
     */
    @Named("userFromId")
    public User mapUserFromId(Long id) {
        if (id == null) {
            return null;
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
    }

    /**
     * Converts TaskDto to a TaskStatus entity.
     * This helper method is final and should not be overridden.
     * <p>
     * Resolution order: by statusId, by status slug, default from configuration
     * </p>
     *
     * @param dto TaskDto object
     * @return TaskStatus entity
     */
    @Named("statusFromDto")
    public TaskStatus mapStatusFromDto(TaskDto dto, @Context Task entity) {

        if (dto.getStatusId() != null) {
            return statusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Task status with id " + dto.getStatusId() + " not found"));
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            return statusRepository.findBySlug(dto.getStatus())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Task status '" + dto.getStatus() + "' not found"));
        }

        if (entity != null && entity.getStatus() != null) {
            return entity.getStatus();
        }

        return statusRepository.findBySlug(defaultStatusSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Default status '" + defaultStatusSlug + "' not found"));
    }
}
