package hexlet.code.service.impl;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskService;
import hexlet.code.spec.TaskSpecification;
import hexlet.code.model.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;
    private final TaskStatusRepository statusRepository;

    @Value("${app.default-status:draft}")
    private String defaultStatusSlug;

    /**
     * Retrieves all tasks that match the provided filtering parameters.
     * <p>
     * This method is read-only and should not be overridden.
     * </p>
     *
     * @param params DTO containing filter parameters for searching tasks
     * @return list of {@link TaskDto} objects matching the filter
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getAll(TaskParamsDto params) {
        var spec = taskSpecification.build(params);
        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }


    /**
     * Retrieves a single task by its identifier.
     * <p>
     * Throws {@link ResponseStatusException} with {@code 404 NOT FOUND}
     * if the task does not exist.
     * </p>
     *
     * @param id the ID of the task to retrieve
     * @return the corresponding {@link TaskDto} if found
     */
    @Override
    @Transactional(readOnly = true)
    public TaskDto getById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return taskMapper.toDto(task);
    }

    /**
     * Creates a new task entity based on the provided DTO.
     * <p>
     * If no status is provided, the default status is applied.
     * </p>
     *
     * @param dto the {@link TaskDto} containing task data to persist
     * @return the newly created {@link TaskDto}
     */
    @Override
    @Transactional
    public TaskDto create(TaskDto dto) {
        var entity = taskMapper.toEntity(dto);
        applyDefaultStatusIfNull(entity);
        var saved = taskRepository.save(entity);
        return taskMapper.toDto(saved);
    }
    /**
     * Updates an existing task with new data from the provided DTO.
     * <p>
     * The existing task is fetched, updated, and persisted.
     * Throws {@link ResponseStatusException} with {@code 404 NOT FOUND}
     * if the task does not exist.
     * </p>
     *
     * @param id  the ID of the task to update
     * @param dto the {@link TaskDto} containing updated task data
     * @return the updated {@link TaskDto}
     */
    @Override
    @Transactional
    public TaskDto update(Long id, TaskDto dto) {
        var entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (dto.getLabelIds() == null) {
            dto.setLabelIds(
                    entity.getLabels().stream()
                            .map(Label::getId)
                            .collect(Collectors.toSet())
            );
        }

        taskMapper.update(dto, entity, entity);
        var updated = taskRepository.save(entity);
        return taskMapper.toDto(updated);
    }

    /**
     * Deletes a task by its identifier.
     * <p>
     * Throws {@link ResponseStatusException} with {@code 404 NOT FOUND}
     * if the task does not exist.
     * </p>
     *
     * @param id the ID of the task to delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskRepository.delete(task);
    }

    private void applyDefaultStatusIfNull(Task entity) {
        if (entity.getStatus() == null) {
            var defaultStatus = statusRepository.findBySlug(defaultStatusSlug)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Default status '" + defaultStatusSlug + "' not found"));
            entity.setStatus(defaultStatus);
        }
    }
}
