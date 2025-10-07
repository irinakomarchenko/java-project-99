package hexlet.code.controllers;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@Tag(name = "Task Statuses", description = "Task status management")
@RequiredArgsConstructor
public final class TaskStatusController {

    private final TaskStatusService service;

    @GetMapping
    public ResponseEntity<List<TaskStatusDto>> getAll() {
        List<TaskStatusDto> statuses = service.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statuses.size()))
                .body(statuses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDto> getById(@PathVariable Long id) {
        var status = service.findById(id);
        return ResponseEntity.ok(status);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<TaskStatusDto> create(@Valid @RequestBody TaskStatusDto dto) {
        TaskStatusDto created = service.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
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
