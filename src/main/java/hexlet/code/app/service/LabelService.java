package hexlet.code.app.service;

import hexlet.code.app.dto.LabelDto;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class LabelService {

    private final LabelRepository repository;
    private final LabelMapper mapper;

    public List<LabelDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    public LabelDto getById(Long id) {
        Label label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        return mapper.toDto(label);
    }

    public LabelDto create(LabelDto dto) {
        if (repository.existsByName(dto.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Label already exists");
        }
        Label label = mapper.toEntity(dto);
        return mapper.toDto(repository.save(label));
    }

    public LabelDto update(Long id, LabelDto dto) {
        Label label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        mapper.update(dto, label);
        return mapper.toDto(repository.save(label));
    }

    public void delete(Long id) {
        Label label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));

        if (!label.getTasks().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Label is linked to tasks");
        }
        repository.delete(label);
    }
}
