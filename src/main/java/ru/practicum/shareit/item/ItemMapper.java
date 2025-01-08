package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static Item toItem(CreateItemRequest createItemRequest) {
        return Item.builder()
                .name(createItemRequest.getName())
                .description(createItemRequest.getDescription())
                .available(createItemRequest.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }


}
