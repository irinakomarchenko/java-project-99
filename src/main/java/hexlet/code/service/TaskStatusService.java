package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskStatusService {

    private final TaskStatusRepository repository;
    private final TaskStatusMapper mapper;
    private final TaskRepository taskRepository;

    public List<TaskStatusDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public TaskStatusDto findById(Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
        return mapper.toDto(taskStatus);
    }

    public TaskStatusDto create(TaskStatusDto dto) {
        var entity = mapper.toEntity(dto);
        var saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public TaskStatusDto update(Long id, TaskStatusDto dto) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
        mapper.update(dto, taskStatus);
        var saved = repository.save(taskStatus);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));

        if (taskRepository.existsByStatusId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Cannot delete status linked to existing tasks"
            );
        }

        repository.delete(status);
    }
}
