package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getById(long id) {
        return userRepository.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public User addUser(User user) {
        log.trace("Добавление пользователя {}", user);
        return userRepository.saveUser(user);
    }

    public User updateUserBuId(long id, UserUpdateRequest userUpdateRequest) {
        log.trace("Обновление пользователя с номером {} {}", id, userUpdateRequest);
        User savedUser = this.getById(id);

        if (userUpdateRequest.getEmail() != null) {
            savedUser.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getName() != null) {
            savedUser.setName(userUpdateRequest.getName());
        }
        return userRepository.saveUser(savedUser);
    }

    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }
}
