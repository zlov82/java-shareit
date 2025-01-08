package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private ItemRequest request;
}
