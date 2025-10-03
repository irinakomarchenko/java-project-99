package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LabelDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = 1000)
    private String name;

    private LocalDate createdAt;
}
