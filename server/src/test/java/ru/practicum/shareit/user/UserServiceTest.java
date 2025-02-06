package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder()
                .name("Sam")
                .email("sam@mail.com")
                .build());

        user2 = userRepository.save(User.builder()
                .name("Alice")
                .email("alice@mail.com")
                .build());
    }

    @Test
    void whenGetById_thenReturn() {
        User myUser = userRepository.save(User.builder()
                .name("Andrey")
                .email("kozlov@mail.com")
                .build());

        User user = userService.getById(myUser.getId());
        assertNotNull(user);
        assertEquals(myUser.getId(), user.getId());
        assertEquals(myUser.getName(), user.getName());
        assertEquals(myUser.getEmail(), user.getEmail());
    }

    @Test
    void whenGetByIdForNonExistingUser_thenThrowNotFoundException() {
        long nonExistingId = 999L;
        assertThrows(NotFoundException.class, () -> userService.getById(nonExistingId));
    }

    @Test
    void whenEditById_thenReturnEditedUserDto() {
        UserUpdateRequest update = UserUpdateRequest.builder()
                .name("Samuel")
                .email("samuel@mail.com")
                .build();
        User updated = userService.updateUserById(user1.getId(), update);
        assertNotNull(updated);
        assertEquals("Samuel", updated.getName());
        assertEquals("samuel@mail.com", updated.getEmail());
    }

    @Test
    void whenEditByIdWithWromgUser_thenThrowNotFoundException() {
        UserUpdateRequest update = UserUpdateRequest.builder()
                .email(user2.getEmail())
                .build();

        assertThrows(NotFoundException.class, () -> userService.updateUserById(999L, update));
    }

    @Test
    void whenCreateUser_thenReturnCreatedUserDto() {
        UserCreateRequest newUser = UserCreateRequest.builder()
                .name("Bob")
                .email("bob@mail.com")
                .build();
        User created = userService.addUser(newUser);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Bob", created.getName());
        assertEquals("bob@mail.com", created.getEmail());
    }

    @Test
    void whenCreateUserWithDuplicateEmail_thenThrowDublicatingEmailException() {
        UserCreateRequest newUser = UserCreateRequest.builder()
                .name("Another Sam")
                .email(user1.getEmail())
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(newUser));
    }

    @Test
    void whenDeleteById_thenUserIsDeleted() {
        userService.deleteUserById(user1.getId());
        assertThrows(NotFoundException.class, () -> userService.getById(user1.getId()));
    }
}
