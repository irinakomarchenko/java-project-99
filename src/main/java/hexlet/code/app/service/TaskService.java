package hexlet.code.app.service;

import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.dto.TaskParamsDto;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.spec.TaskSpecification;
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
    private final TaskMapper taskMapper;
    private final LabelRepository labelRepository;
    private final TaskSpecification taskSpecification;

    public List<TaskDto> getAll(TaskParamsDto params) {
        var spec = taskSpecification.build(params);
        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    public TaskDto getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return taskMapper.toDto(task);
    }

    public TaskDto create(TaskDto dto) {
        Task task = taskMapper.toEntity(dto);

        if (dto.getStatusId() != null) {
            TaskStatus status = statusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));
            task.setStatus(status);
        }
        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
            task.setAssignee(assignee);
        }
        if (dto.getLabelIds() != null) {
            Set<Label> labels = dto.getLabelIds().stream()
                    .map(id -> labelRepository.findById(id)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found")))
                    .collect(Collectors.toSet());
            task.setLabels(labels);
        }

        return taskMapper.toDto(taskRepository.save(task));
    }

    public TaskDto update(Long id, TaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        taskMapper.update(dto, task);

        if (dto.getStatusId() != null) {
            TaskStatus status = statusRepository.findById(dto.getStatusId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));
            task.setStatus(status);
        }

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
            task.setAssignee(assignee);
        }

        if (dto.getLabelIds() != null) {
            Set<Label> labels = dto.getLabelIds().stream()
                    .map(labelId -> labelRepository.findById(id)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found")))
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
