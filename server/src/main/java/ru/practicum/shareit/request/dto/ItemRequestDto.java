package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private Long requestor;
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;
}
