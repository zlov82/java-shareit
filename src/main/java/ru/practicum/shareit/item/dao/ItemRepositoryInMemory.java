package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {

    private final Map<Long, Item> itemDb = new HashMap<>();
    private long itemsCounter = 0L;

    @Override
    public Item saveItem(Item item) {
        item.setId(++itemsCounter);
        itemDb.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        itemDb.put(item.getId(),item);
        return item;
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return Optional.ofNullable(itemDb.get(itemId));
    }

    @Override
    public List<Item> getAllByUser(long userId) {
        return itemDb.values().stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemDb.values().stream()
                .filter(item -> item.getName().toUpperCase().contains(text.toUpperCase()) ||
                        item.getDescription().toUpperCase().contains(text.toUpperCase()))
                .filter(item -> item.isAvailable())
                .toList();
    }
}
