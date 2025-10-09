package hexlet.code.service.impl;

import hexlet.code.dto.LabelDto;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class LabelServiceImpl implements LabelService {

    private final LabelRepository repository;
    private final LabelMapper mapper;

    @Override
    public List<LabelDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public LabelDto getById(Long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        return mapper.toDto(label);
    }

    @Override
    public LabelDto create(LabelDto dto) {
        var existing = repository.findByName(dto.getName());
        if (existing.isPresent()) {
            return mapper.toDto(existing.get());
        }
        var label = mapper.toEntity(dto);
        var saved = repository.save(label);
        return mapper.toDto(saved);
    }

    @Override
    public LabelDto update(Long id, LabelDto dto) {
        var label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        mapper.update(dto, label);
        var saved = repository.save(label);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Label not found"));
        if (!label.getTasks().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot delete label with tasks");
        }
        repository.delete(label);
    }
}
