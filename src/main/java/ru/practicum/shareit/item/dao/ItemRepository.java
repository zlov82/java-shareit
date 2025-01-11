package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item saveItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getById(long itemId);

    List<Item> getAllByUser(long userId);

    List<Item> searchItems(String text);
}
