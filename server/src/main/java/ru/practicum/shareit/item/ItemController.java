package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping()
    public List<ItemDto> getAllItemsByUsers(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("====Получение всех вещей пользователя Id = {}", userId);
        return itemService.getAllByUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        log.info("====Получение вещи с id = {}", itemId);
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("====Поиск вещи");
        if (text.isEmpty()) {
            return List.of();
        }

        return itemService.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @PostMapping()
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody CreateItemRequest createItemRequest) {
        log.info("====Сохранение вещи");
        Item item = itemService.saveItem(userId, createItemRequest);
        return ItemMapper.toItemDto(item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CreateCommentRequest commentRequest) {

        Item item = itemService.getItemById(itemId);
        return ItemMapper.commentDto(commentService.addComment(userId, item, commentRequest.getText()));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody UpdateItemRequest updateItemRequest) {
        log.info("====Обновление данных вещи id = {}\n{}", itemId, updateItemRequest);
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, updateItemRequest));
    }
}
