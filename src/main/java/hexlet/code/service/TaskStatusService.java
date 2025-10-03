package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public final class TaskStatusService {

    private final TaskStatusRepository repository;
    private final TaskStatusMapper mapper;
    private final TaskRepository taskRepository;

    public Page<TaskStatusDto> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    public TaskStatusDto getById(Long id) {
        TaskStatus status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskStatus not found"));
        return mapper.toDto(status);
    }

    public TaskStatusDto create(TaskStatusDto dto) {
        TaskStatus status = mapper.toEntity(dto);
        return mapper.toDto(repository.save(status));
    }

    public TaskStatusDto update(Long id, TaskStatusDto dto) {
        TaskStatus status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskStatus not found"));

        mapper.update(dto, status);
        return mapper.toDto(repository.save(status));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskStatus not found");
        }

        if (taskRepository.existsByStatusId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete status with assigned tasks");
        }
        repository.deleteById(id);
    }
}

