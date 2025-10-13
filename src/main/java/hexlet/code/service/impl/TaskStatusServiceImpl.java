package hexlet.code.service.impl;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository repository;
    private final TaskStatusMapper mapper;

    @Override
    public List<TaskStatusDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public TaskStatusDto findById(Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
        return mapper.toDto(status);
    }

    @Override
    public TaskStatusDto create(TaskStatusDto dto) {
        var entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public TaskStatusDto update(Long id, TaskStatusDto dto) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
        mapper.update(dto, status);
        repository.save(status);
        return mapper.toDto(status);
    }

    @Override
    public void delete(Long id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task status not found"));
        repository.delete(status);
    }
}
