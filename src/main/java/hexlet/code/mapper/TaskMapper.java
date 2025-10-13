package hexlet.code.mapper;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Mapping(target = "status", source = "status.slug")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "labelsToIds")
    public abstract TaskDto toDto(Task entity);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    public abstract Task toEntity(TaskDto dto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
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
     * Maps TaskDto to Task entity with resolved relationships.
     * <p>
     * This method is final to prevent unsafe overriding.
     *
     * @param dto TaskDto containing task data
     * @return fully constructed Task entity
     */
    public Task map(TaskDto dto) {
        Task task = toEntity(dto);

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            task.setTitle("Untitled Task");
        }
        if (dto.getContent() == null) {
            task.setContent("");
        }

        task.setStatus(resolveStatus(dto));
        task.setAssignee(resolveAssignee(dto));
        task.setLabels(resolveLabels(dto));

        return task;
    }

    private TaskStatus resolveStatus(TaskDto dto) {
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
        return statusRepository.findBySlug("draft")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Default status 'draft' not found"));
    }

    private User resolveAssignee(TaskDto dto) {
        if (dto.getAssigneeId() == null) {
            return null;
        }
        return userRepository.findById(dto.getAssigneeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
    }

    private Set<Label> resolveLabels(TaskDto dto) {
        if (dto.getLabelIds() == null || dto.getLabelIds().isEmpty()) {
            return Set.of();
        }
        return dto.getLabelIds().stream()
                .map(id -> labelRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Label with id " + id + " not found")))
                .collect(Collectors.toSet());
    }
}
