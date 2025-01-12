package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.UserRepositoryInMemory;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryInMemory();
    }

    @Test
    void saveUser_and_getUser() {
        User user = userRepository.saveUser(createUser());
        Optional<User> optSavedUser = userRepository.getUserById(user.getId());
        assertTrue(optSavedUser.isPresent());
        assertEquals(user.getName(), optSavedUser.get().getName());
    }

    @Test
    void deleteUser() {
        User user1 = userRepository.saveUser(createUser());
        User user2 = userRepository.saveUser(createUser());
        userRepository.deleteUserById(user1.getId());

        Optional<User> optSavedUser1 = userRepository.getUserById(user1.getId());
        Optional<User> optSavedUser2 = userRepository.getUserById(user2.getId());

        assertTrue(optSavedUser1.isEmpty());
        assertTrue(optSavedUser2.isPresent());
    }


    private User createUser() {
        Random random = new Random();
        return User.builder()
                .name(random.ints(10).toString())
                .email(random.ints(5).toString() + "@mail.ru")
                .build();
    }

}
