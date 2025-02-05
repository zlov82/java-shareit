package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemShortDto;

import java.util.List;

public class ItemMapper {

    public static Item toItem(CreateItemRequest createItemRequest) {
        return Item.builder()
                .name(createItemRequest.getName())
                .description(createItemRequest.getDescription())
                .available(createItemRequest.getAvailable())
                .build();
    }

    public static ItemDto toItemDto(Item item) {

        List<CommentDto> commentDtoList = item.getComments().stream()
                .map(ItemMapper::commentDto)
                .toList();

        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner().getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .comments(commentDtoList)
                .lastBooking(item.getLastBooking())
                .nextBooking(item.getNextBooking())
                .build();
    }

    public static CommentDto commentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }


    public static ItemShortDto toShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
