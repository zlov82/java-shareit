package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dao.ItemRepositoryInMemory;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItemRepositoryTest {

    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        itemRepository = new ItemRepositoryInMemory();
    }

    @Test
    void saveAndGetItem() {

        Item item = itemRepository.saveItem(createItem(1L, true));
        Optional<Item> optSavedItem = itemRepository.getById(item.getId());

        assertTrue(optSavedItem.isPresent());
        assertEquals(item.getName(), optSavedItem.get().getName());
    }

    @Test
    void getAllItemsSameUser() {
        Item item1 = itemRepository.saveItem(createItem(1L, true));
        Item item2 = itemRepository.saveItem(createItem(1L, true));
        Item item3 = itemRepository.saveItem(createItem(2L, true));

        List<Item> userItemsList = itemRepository.getAllByUser(1L);
        assertEquals(2, userItemsList.size()); //
    }

    @Test
    void updateItem() {
        Item item = itemRepository.saveItem(createItem(1L, false));
        item.setAvailable(true);
        itemRepository.saveItem(item);

        Optional<Item> optSaveItem = itemRepository.getById(item.getId());

        assertTrue(optSaveItem.isPresent());
        assertTrue(optSaveItem.get().isAvailable());

    }

    private Item createItem(long ownerId, boolean status) {
        Random random = new Random();

        return Item.builder()
                .name(random.ints(10).toString())
                .owner(ownerId)
                .description(random.ints(30).toString())
                .available(status)
                .build();
    }
}
