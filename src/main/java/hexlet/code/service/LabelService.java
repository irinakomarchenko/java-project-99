package hexlet.code.service;

import hexlet.code.dto.LabelDto;

public interface LabelService {
    java.util.List<LabelDto> getAll();

    LabelDto getById(Long id);

    LabelDto create(LabelDto dto);

    LabelDto update(Long id, LabelDto dto);

    void delete(Long id);
}
