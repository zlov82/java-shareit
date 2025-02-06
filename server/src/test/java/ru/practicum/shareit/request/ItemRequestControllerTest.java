package ru.practicum.shareit.request;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    private Long userId;
    private Long userId2;
    private Long requestId;
    private CreateItemRequestDto requestInput;
    private ItemRequest requestOutput;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {
        userId = 1L;
        userId2 = 2L;
        requestId = 2L;

        user = User.builder()
                .id(userId)
                .name("Andrey")
                .email("kozlov@mail.com")
                .build();

        user2 = User.builder()
                .id(userId2)
                .name("Maxim")
                .email("drugoi@mail.com")
                .build();

        requestInput = CreateItemRequestDto.builder()
                .description("Станок для заточки ножей")
                .build();

        requestOutput = ItemRequest.builder()
                .id(requestId)
                .requestor(user)
                .description("Станок для заточки ножей")
                .items(List.of(
                        Item.builder()
                                .id(10L)
                                .name("Станок за заточки")
                                .available(true)
                                .owner(user2)
                                .build()
                ))
                .created(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();
    }

  /*  @Test
    void create() throws Exception {

        when(itemRequestService.createRequest(userId, CreateItemRequestDto.builder().build())).thenReturn(requestOutput);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(CreateItemRequestDto.builder().build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Станок для заточки ножей"))
                .andExpect(jsonPath("$.items[0].name").value("Станок за заточки"));

        verify(itemRequestService, times(1)).createRequest(userId, CreateItemRequestDto.builder().build());
    }*/

    @Test
    void getRequests() throws Exception {
        when(itemRequestService.getUserRequests(userId)).thenReturn(List.of(requestOutput));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Станок для заточки ножей"));

        verify(itemRequestService, times(1)).getUserRequests(userId);
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAnotherUserRequest(userId)).thenReturn(List.of(requestOutput));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId));

        verify(itemRequestService, times(1)).getAnotherUserRequest(userId);
    }

    @Test
    void getRequest() throws Exception {
        when(itemRequestService.getRequestById(requestId)).thenReturn(requestOutput);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Станок для заточки ножей"));

        verify(itemRequestService, times(1)).getRequestById(requestId);
    }
}
