package hexlet.code.app.controllers;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService service;

    @GetMapping
    public List<LabelDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<LabelDto> create(@Valid @RequestBody LabelDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<LabelDto> update(@PathVariable Long id, @Valid @RequestBody LabelDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
