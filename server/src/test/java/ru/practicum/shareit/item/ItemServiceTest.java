package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentService commentService;

    private User user;
    private User booker;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setup() {
        user = userRepository.save(User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build());

        booker = userRepository.save(User.builder()
                .email("test2@mail.com")
                .name("Booker")
                .build());

        item1 = itemRepository.save(Item.builder()
                .owner(user)
                .name("item1")
                .description("description1")
                .available(true)
                .build());

        item2 = itemRepository.save(Item.builder()
                .owner(user)
                .name("Item2")
                .description("Desc2")
                .available(true)
                .build());
    }


    @Test
    void whenGetUserItems_thenReturnCorrectItems() {
        List<Item> items = itemService.getAllByUser(user.getId());
        assertNotNull(items);
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item1.getName())));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item2.getName())));
    }

    @Test
    void whenCreateComment_thenReturnCorrectCommentRespondDto() {
        LocalDateTime now = LocalDateTime.now().minusSeconds(3);

        Booking booking = Booking.builder()
                .user(booker)
                .status(BookingStatus.APPROVED)
                .item(item1)
                .start(now.plusSeconds(1))
                .end(now.plusSeconds(2))
                .build();

        bookingRepository.save(booking);

        Comment comment = Comment.builder()
                .text("Comment")
                .build();

        Comment commentResponse = commentService.addComment(booker.getId(), item1, comment.getText());

        assertNotNull(commentResponse);
        assertEquals(commentResponse.getAuthor().getName(), booker.getName());
        assertEquals(commentResponse.getText(), comment.getText());
    }

    @Test
    void whenEditItem_thenReturnEditedItemDto() {
        UpdateItemRequest update = UpdateItemRequest.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(false)
                .build();

        Item updatedDto = itemService.updateItem(user.getId(), item1.getId(), update);

        assertNotNull(updatedDto);
        assertEquals("UpdatedName", updatedDto.getName());
        assertEquals("UpdatedDescription", updatedDto.getDescription());
    }

    @Test
    void whenEditItemWithNonOwner_thenThrowOwnerException() {
        UpdateItemRequest update = UpdateItemRequest.builder()
                .name("NewName")
                .build();

        assertThrows(NotFoundException.class, () -> itemService.updateItem(booker.getId(), item1.getId(), update));
    }

    @Test
    void whenEditItemWithNonItem_thenThrowNotFoundException() {
        UpdateItemRequest update = UpdateItemRequest.builder()
                .name("NewName")
                .build();

        assertThrows(NotFoundException.class, () -> itemService.updateItem(booker.getId(), 999L, update));
    }

    @Test
    void whenEditItemWithNonUser_thenThrowNotFoundException() {
        UpdateItemRequest update = UpdateItemRequest.builder()
                .name("NewName")
                .build();

        assertThrows(NotFoundException.class, () -> itemService.updateItem(999L, item1.getId(), update));
    }

    @Test
    void whenSearchByText_thenReturnMatchingItems() {
        List<Item> result = itemService.search("item1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1.getName(), result.getFirst().getName());
    }

    @Test
    void whenSearchByEmptyText_thenReturnEmptyList() {
        List<Item> result = itemService.search("   ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenCreateItem_thenReturnCreatedItemDto() {
        CreateItemRequest newItemDto = CreateItemRequest.builder()
                .name("NewItem")
                .description("NewDescription")
                .available(true)
                .build();

        Item created = itemService.saveItem(user.getId(), newItemDto);

        assertNotNull(created);
        assertEquals("NewItem", created.getName());
        assertEquals("NewDescription", created.getDescription());
    }

    @Test
    void whenGetByIdAndOwnerIdWithNonExistingItem_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L));
    }
}
