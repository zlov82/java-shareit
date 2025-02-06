package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Builder
@Data
public class BookingDto {
    private Long id;
    private ItemDto item;

    private LocalDateTime start;

    private LocalDateTime end;
    private UserDto booker;
    private String status;
}
