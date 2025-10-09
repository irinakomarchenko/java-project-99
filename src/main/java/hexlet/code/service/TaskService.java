package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskParamsDto;

import java.util.List;

public interface TaskService {

    List<TaskDto> getAll(TaskParamsDto params);

    TaskDto getById(Long id);

    TaskDto create(TaskDto dto);

    TaskDto update(Long id, TaskDto dto);

    void delete(Long id);

}
