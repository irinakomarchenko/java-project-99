package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import java.util.List;

public interface TaskStatusService {
    List<TaskStatusDto> getAll();

    TaskStatusDto findById(Long id);

    TaskStatusDto create(TaskStatusDto dto);

    TaskStatusDto update(Long id, TaskStatusDto dto);

    void delete(Long id);
}

