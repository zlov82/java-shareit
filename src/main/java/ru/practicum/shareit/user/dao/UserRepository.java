package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserRepository {
    User saveUser(User user);

    Optional<User> getUserById(long id);

    void deleteUserById(long id);
}
