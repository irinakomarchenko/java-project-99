package hexlet.code.app.controllers;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.service.TaskStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@Tag(name = "Task Statuses", description = "Task status management")
@RequiredArgsConstructor
public class TaskStatusController {

    private final TaskStatusService service;

    @GetMapping
    public List<TaskStatusDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TaskStatusDto> create(@Valid @RequestBody TaskStatusDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDto> update(@PathVariable Long id, @Valid @RequestBody TaskStatusDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
