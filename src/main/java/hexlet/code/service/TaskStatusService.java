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

    private String generateSlug(String name) {
        if (name == null) {
            return null;
        }
        return name.trim().toLowerCase().replace(' ', '_');
    }

    public Page<TaskStatusDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    public TaskStatusDto getById(Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
        return mapper.toDto(status);
    }

    public TaskStatusDto create(TaskStatusDto dto) {
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            dto.setSlug(generateSlug(dto.getName()));
        }
        var existing = repository.findBySlug(dto.getSlug()).orElse(null);
        if (existing != null) {
            return mapper.toDto(existing);
        }
        var entity = mapper.toEntity(dto);
        var saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    public TaskStatusDto update(Long id, TaskStatusDto dto) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
        String incomingSlug = dto.getSlug();
        mapper.update(dto, status);
        if (incomingSlug != null) {
            status.setSlug(incomingSlug);
        }
        var saved = repository.save(status);
        return mapper.toDto(saved);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found");
        }
        if (taskRepository.existsByStatusId(id)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Cannot delete status assigned to existing tasks");
        }
        repository.deleteById(id);
    }
}
