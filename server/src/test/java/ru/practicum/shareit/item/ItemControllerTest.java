package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    private User user;
    private Long userId = 1L;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .name("Sam")
                .email("sam@mail.ru")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("item1")
                .description("Description1")
                .available(true)
                .comments(new ArrayList<>())
                .owner(user)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("item2")
                .description("Description2")
                .available(true)
                .owner(user)
                .build();
    }

    @Test
    void getAll() throws Exception {
        List<Item> items = List.of(item1, item2);

        when(itemService.getAllByUser(userId)).thenReturn(items);

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$").isArray());

        verify(itemService, times(1)).getAllByUser(userId);
    }

    @Test
    void getOne() throws Exception {

        when(itemService.getItemById(2)).thenReturn(item2);

        mockMvc.perform(get("/items/{id}", 2)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(jsonPath("$.id").value(item2.getId()))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getItemById(2);
    }

    @Test
    void searchAllByText() throws Exception {
        List<Item> items = List.of(item1, item2);

        String text = "item";

        when(itemService.search(text)).thenReturn(items);

        mockMvc.perform(get("/items/search").param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$").isArray());

        verify(itemService, times(1)).search(text);
    }

    @Test
    void editOne() throws Exception {
        Item editedItem = Item.builder()
                .id(1L)
                .name("editedItem1")
                .description("editedItemDescription1")
                .available(true)
                .owner(user)
                .build();

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .name(editedItem.getName())
                .description(editedItem.getDescription())
                .available(editedItem.isAvailable())
                .build();

        when(itemService.updateItem(userId, editedItem.getId(), updateItemRequest)).thenReturn(editedItem);

        mockMvc.perform(patch("/items/{id}", item1.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(editedItem.getName()));

        verify(itemService, times(1)).updateItem(userId, editedItem.getId(), updateItemRequest);
    }

    /*@Test
    void create() throws Exception {

        Item saveItem = Item.builder()
                .id(3L)
                .name("saved")
                .description("Description3")
                .available(true)
                .owner(user)
                .build();

        CreateItemRequest request = CreateItemRequest.builder()
                .name("Name")
                .description("Description")
                .available(true)
                .build();

        when(itemService.saveItem(userId, request)).thenReturn(saveItem);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(new ObjectMapper().writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(item1.getName()));

        verify(itemService, times(1)).saveItem(userId, request);
    }*/
}
