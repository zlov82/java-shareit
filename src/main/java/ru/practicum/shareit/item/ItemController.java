package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

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
                              @Valid @RequestBody CreateItemRequest createItemRequest) {
        log.info("====Сохранение вещи");
        return ItemMapper.toItemDto(itemService.saveItem(userId, createItemRequest));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @Valid @RequestBody UpdateItemRequest updateItemRequest) {
        log.info("====Обновление данных вещи id = {}\n{}", itemId, updateItemRequest);
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, updateItemRequest));
    }
}
