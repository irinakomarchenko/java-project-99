package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.mapper.TaskStatusMapper;
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
        return repository.findAll(pageable).map(mapper::toDto);
    }

    public TaskStatusDto getById(Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskStatus not found"));
        return mapper.toDto(status);
    }

    public TaskStatusDto create(TaskStatusDto dto) {
        var existing = repository.findBySlug(dto.getSlug());
        if (existing.isPresent()) {
            return mapper.toDto(existing.get());
        }
        var status = mapper.toEntity(dto);
        var saved = repository.save(status);
        return mapper.toDto(saved);
    }

    public TaskStatusDto update(Long id, TaskStatusDto dto) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskStatus not found"));
        mapper.update(dto, status);
        var saved = repository.save(status);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "TaskStatus not found");
        }
        if (taskRepository.existsByStatusId(id)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot delete status with tasks");
        }
        repository.deleteById(id);
    }
}
