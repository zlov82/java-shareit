package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    String QUERY_ITEM_SEARCH = """
            select i from Item i
            where i.available = true
            and (upper(i.name) like upper(%:text%) or upper(i.description) like upper(%:text%))
            """;


    List<Item> findAllByOwner(User user);

    @Query(QUERY_ITEM_SEARCH)
    List<Item> searchItemsByNameAndDescription(@Param("text") String text);
}

