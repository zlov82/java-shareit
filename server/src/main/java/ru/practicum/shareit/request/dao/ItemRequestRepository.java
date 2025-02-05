package ru.practicum.shareit.request.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequestorOrderByCreatedDesc(User user);

    @Query("""
            select ir
            from ItemRequest as ir
            where ir.requestor <> :user
            order by ir.created desc
            """)
    List<ItemRequest> findAllByAnotherUser(@Param("user") User user);
}
