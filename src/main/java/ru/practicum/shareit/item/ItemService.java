package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CommentService commentService;

    public Item saveItem(long userId, CreateItemRequest createItemRequest) {
        User owner = userService.getById(userId);
        Item item = ItemMapper.toItem(createItemRequest);
        item.setOwner(owner);
        item.setComments(new ArrayList<>());
        log.info("Вещь для сохранения: {}", item);
        return itemRepository.save(item);
    }


    public Item getItemById(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        List<Comment> commentList = new ArrayList<>();
        commentList = commentService.getItemComments(item);
        log.info("Получены комментарии {}", commentList);
        item.setComments(commentList);
        log.info("Вещь для возвращения {}", item);
        return item;
    }

    public List<Item> getAllByUser(long userId) {
        User owner = userService.getById(userId);
        return itemRepository.findAllByOwner(owner);
    }

    public Item updateItem(long userId, long itemId, UpdateItemRequest updateItemRequest) {
        User owner = userService.getById(userId);
        log.info("Владелец вещи {}", owner);
        Item savedItem = this.getItemById(itemId);
        log.info("Найдена вещь для обновления: {}", savedItem);

        if (!savedItem.getOwner().equals(owner)) {
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
        return itemRepository.save(savedItem);
    }

    public List<Item> search(String text) {
        List<Item> listItems = itemRepository.searchItemsByNameAndDescription(text);
        log.info("Результат поиска по строке {}\n{}", text, listItems);
        return listItems;
    }
}
