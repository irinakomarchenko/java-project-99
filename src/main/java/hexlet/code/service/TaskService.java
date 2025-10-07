package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParamsDto;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.spec.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public final class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusRepository statusRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;
    private final TaskSpecification taskSpecification;

    public List<TaskDto> getAll(TaskParamsDto params) {
        var spec = taskSpecification.build(params);
        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public TaskDto getById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return taskMapper.toDto(task);
    }

    public TaskDto create(TaskDto dto) {
        var task = taskMapper.toEntity(dto);

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            task.setTitle("Untitled Task");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            task.setContent("");
        }

        TaskStatus status;

        if (dto.getStatusId() != null) {
            status = statusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Task status with id " + dto.getStatusId() + " not found"));
        } else if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            status = statusRepository.findBySlug(dto.getStatus())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Task status '" + dto.getStatus() + "' not found"));
        } else {
            status = statusRepository.findBySlug("draft")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Default status 'draft' not found"));
        }

        task.setStatus(status);

        if (dto.getAssigneeId() != null) {
            var assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
            task.setAssignee(assignee);
        }

        if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
            Set<Label> labels = dto.getLabelIds().stream()
                    .map(id -> labelRepository.findById(id)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Label with id " + id + " not found")))
                    .collect(Collectors.toSet());
            task.setLabels(labels);
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    public TaskDto update(Long id, TaskDto dto) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        taskMapper.update(dto, task);

        if (dto.getStatusId() != null) {
            var status = statusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Task status with id " + dto.getStatusId() + " not found"));
            task.setStatus(status);
        } else if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            var status = statusRepository.findBySlug(dto.getStatus())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Task status '" + dto.getStatus() + "' not found"));
            task.setStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            var assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
            task.setAssignee(assignee);
        }

        if (dto.getLabelIds() != null) {
            Set<Label> labels = dto.getLabelIds().stream()
                    .map(labelId -> labelRepository.findById(labelId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "Label with id " + labelId + " not found")))
                    .collect(Collectors.toSet());
            task.setLabels(labels);
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        taskRepository.deleteById(id);
    }
}
