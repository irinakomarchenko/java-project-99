package hexlet.code.controllers;

import hexlet.code.dto.LabelDto;
import hexlet.code.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.List;

/**
 * REST controller for managing labels.
 * Provides CRUD operations for label entities.
 */
@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService service;

    /**
     * Returns all labels.
     *
     * @return list of labels with total count header
     */
    @GetMapping
    public ResponseEntity<List<LabelDto>> getAll() {
        List<LabelDto> labels = service.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(labels);
    }

    /**
     * Returns a label by ID.
     *
     * @param id label ID
     * @return label with the given ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<LabelDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Creates a new label (authentication required).
     *
     * @param dto label data
     * @return created label with location header
     */
    @PostMapping
    public ResponseEntity<LabelDto> create(@Valid @RequestBody LabelDto dto) {
        LabelDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates an existing label (authentication required).
     *
     * @param id  label ID
     * @param dto updated label data
     * @return updated label
     */
    @PutMapping("/{id}")
    public ResponseEntity<LabelDto> update(@PathVariable Long id, @Valid @RequestBody LabelDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    /**
     * Deletes a label by ID (authentication required).
     *
     * @param id label ID
     * @return empty response with HTTP 204
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
