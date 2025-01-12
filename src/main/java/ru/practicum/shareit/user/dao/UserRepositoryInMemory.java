package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailValidationException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserRepositoryInMemory implements UserRepository {

    private final Map<Long, User> userDb = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private long userCounter = 0L;

    @Override
    public User saveUser(User user) {

        if (emailUniqSet.contains(user.getEmail())) {
            throw new EmailValidationException("Email ранее уже использовался");
        }
        user.setId(++userCounter);
        emailUniqSet.add(user.getEmail());
        userDb.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        final String email = user.getEmail();
        userDb.computeIfPresent(user.getId(), (id, u) -> {
                    if (!email.equals(u.getEmail())) {
                        if (emailUniqSet.contains(email)) {
                            throw new EmailValidationException("Email: " + email + " already exists");
                        }
                        emailUniqSet.remove(u.getEmail());
                        emailUniqSet.add(email);
                    }
                    return user;
                }
        );
        return user;
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(userDb.get(id));
    }

    @Override
    public void deleteUserById(long id) {
        Optional<User> optionalUser = this.getUserById(id);
        if (optionalUser.isPresent()) {
            userDb.remove(id);
            emailUniqSet.remove(optionalUser.get().getEmail());
        }
    }
}