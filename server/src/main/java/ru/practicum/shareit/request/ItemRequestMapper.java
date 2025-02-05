package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.user.User;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(User user, CreateItemRequestDto requestDto) {
        return ItemRequest.builder()
                .description(requestDto.getDescription())
                .requestor(user)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .requestor(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .description(itemRequest.getDescription())
                .items(itemRequest.getItems().stream()
                        .map(ItemMapper::toShortDto)
                        .toList())
                .build();
    }
}
