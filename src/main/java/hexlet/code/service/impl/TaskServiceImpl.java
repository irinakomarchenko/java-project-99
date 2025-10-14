package hexlet.code.service.impl;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskService;
import hexlet.code.spec.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;
    private final TaskStatusRepository statusRepository;

    @Value("${app.default-status:draft}")
    private String defaultStatusSlug;

    @Override
    public List<TaskDto> getAll(TaskParamsDto params) {
        var spec = taskSpecification.build(params);
        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskDto getById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto create(TaskDto dto) {
        var entity = taskMapper.toEntity(dto);
        applyDefaultStatusIfNull(entity);
        var saved = taskRepository.save(entity);
        return taskMapper.toDto(saved);
    }

    @Override
    public TaskDto update(Long id, TaskDto dto) {
        var entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskMapper.update(dto, entity);
        if (entity.getStatus() == null && dto.getStatusId() == null && dto.getStatus() == null) {
            applyDefaultStatusIfNull(entity);
        }

        var updated = taskRepository.save(entity);
        return taskMapper.toDto(updated);
    }

    @Override
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
