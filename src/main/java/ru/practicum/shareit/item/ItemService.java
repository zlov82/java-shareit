package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    public Item saveItem(long userId, CreateItemRequest createItemRequest) {
        User owner = userService.getById(userId);
        Item item = ItemMapper.toItem(createItemRequest);
        item.setOwner(userId);
        log.info("Вещь для сохранения: {}", item);
        return itemRepository.saveItem(item);
    }


    public Item getItemById(long itemId) {
        return itemRepository.getById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    public List<Item> getAllByUser(long userId) {
        User owner = userService.getById(userId);
        return itemRepository.getAllByUser(userId);
    }

    public Item updateItem(long userId, long itemId, UpdateItemRequest updateItemRequest) {
        User owner = userService.getById(userId);
        log.info("Владелец вещи {}", owner);
        Item savedItem = this.getItemById(itemId);
        log.info("Найдена вещь для обновления: {}", savedItem);

        if (savedItem.getOwner() != owner.getId()) {
            throw new NotFoundException("Запрос на изменение вещи не от владельца");
        }

        if (updateItemRequest.getName() != null) {
            savedItem.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.getDescription() != null) {
            savedItem.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.getAvailable() != null) {
            savedItem.setAvailable(updateItemRequest.getAvailable());
        }
        log.info("Обновленная вещь {}", savedItem);
        return itemRepository.updateItem(savedItem);
    }

    public List<Item> search(String text) {
        List<Item> listItems = itemRepository.searchItems(text);
        log.info("Результат поиска по строке {}\n{}", text, listItems);
        return listItems;
    }
}
