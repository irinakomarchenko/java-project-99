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

/**
 * REST controller for managing task statuses.
 * Provides CRUD operations for task statuses.
 * Creation, update, and deletion require authentication.
 */
@RestController
@RequestMapping("/api/task_statuses")
@Tag(name = "Task Statuses", description = "Task status management")
@RequiredArgsConstructor
public class TaskStatusController {

    private final TaskStatusService service;

    /**
     * Returns all task statuses.
     *
     * @return list of all task statuses with total count header
     */
    @GetMapping
    public ResponseEntity<List<TaskStatusDto>> getAll() {
        List<TaskStatusDto> statuses = service.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statuses.size()))
                .body(statuses);
    }

    /**
     * Returns a task status by ID.
     *
     * @param id task status ID
     * @return task status with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDto> getById(@PathVariable Long id) {
        var status = service.findById(id);
        return ResponseEntity.ok(status);
    }

    /**
     * Creates a new task status (authentication required).
     *
     * @param dto task status data
     * @return created task status with location header
     */
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

    /**
     * Updates an existing task status (authentication required).
     *
     * @param id  task status ID
     * @param dto updated task status data
     * @return updated task status
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDto> update(@PathVariable Long id, @Valid @RequestBody TaskStatusDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Deletes a task status by ID (authentication required).
     *
     * @param id task status ID
     * @return empty response with HTTP 204
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
