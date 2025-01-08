package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailValidationException;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class UserRepositoryInMemory implements UserRepository {

    private final HashMap<Long, User> userDb = new HashMap<>();

    @Override
    public User saveUser(User user) {

        if (!notCrossEmail(user)) {
            throw new EmailValidationException("Email ранее уже использовался");
        }

        if (user.getId() == null) {
            long lastUserId = userDb.size();
            user.setId(++lastUserId);
        }

        userDb.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(userDb.get(id));
    }

    @Override
    public void deleteUserById(long id) {
        userDb.remove(id);
    }

    private boolean notCrossEmail(User user) {
        Optional<User> optionalUser = userDb.values().stream()
                .filter(u -> u.getId() != user.getId())
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findAny();
        return optionalUser.isEmpty();
    }
}
