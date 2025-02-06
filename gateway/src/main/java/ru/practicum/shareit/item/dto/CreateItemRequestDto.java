package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItemRequestDto {
    @NotBlank(message = "Наименование вещи обязательно")
    private String name;

    @NotBlank(message = "Описание обязательно")
    private String description;

    @NotNull(message = "Доступность обязательна")
    private Boolean available;

    private Long requestId;
}
