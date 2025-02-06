package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateItemRequest {
    //@NotBlank(message = "Наименование вещи обязательно")
    private String name;

    //@NotBlank(message = "Описание обязательно")
    private String description;

    //@NotNull(message = "Доступность обязательна")
    private Boolean available;

    private Long requestId;
}
