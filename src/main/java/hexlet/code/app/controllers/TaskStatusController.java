package hexlet.code.app.controllers;

import hexlet.code.app.dto.TaskStatusDto;
import hexlet.code.app.service.TaskStatusService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
@Tag(name = "Task Statuses", description = "Task status management")
@RequiredArgsConstructor
public final class TaskStatusController {

    private final TaskStatusService service;

    @GetMapping
    public ResponseEntity<List<TaskStatusDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TaskStatusDto> statuses = service.getAll(pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statuses.getTotalElements()))
                .body(statuses.getContent());
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
