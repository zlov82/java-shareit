package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private User user;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .name("Andrey")
                .email("kozlov@mail.ru")
                .build();
    }

    @Test
    void getById() throws Exception {

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void editById() throws Exception {
        User editedUser = User.builder()
                .id(userId)
                .name("New")
                .email("newuser@mail.ru")
                .build();

        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .name(editedUser.getName())
                .email(editedUser.getEmail())
                .build();

        when(userService.updateUserById(userId, userUpdateRequest)).thenReturn(editedUser);

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).updateUserById(userId, userUpdateRequest);
    }

    @Test
    void create() throws Exception {

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();

        when(userService.addUser(userCreateRequest)).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).addUser(userCreateRequest);
    }

    @Test
    void deleteById() throws Exception {

        doNothing().when(userService).deleteUserById(userId);

        mockMvc.perform(delete("/users/{id}", userId)).andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(userId);
    }
}
