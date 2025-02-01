package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private List<CommentDto> comments;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}
