package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getById(long id) {
        Optional<User> optionalUser = userRepository.getUserById(id);
        if (optionalUser.isPresent()) {
            return User.builder()
                    .id(optionalUser.get().getId())
                    .name(optionalUser.get().getName())
                    .email(optionalUser.get().getEmail())
                    .build();
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public User addUser(UserCreateRequest userCreateRequest) {
        log.trace("Добавление пользователя {}", userCreateRequest);
        User newUser = UserMapper.toUser(userCreateRequest);
        return userRepository.saveUser(newUser);
    }

    public User updateUserById(long id, UserUpdateRequest userUpdateRequest) {
        log.trace("Обновление пользователя с номером {} {}", id, userUpdateRequest);
        User savedUser = this.getById(id);

        if (userUpdateRequest.getEmail() != null) {
            savedUser.setEmail(userUpdateRequest.getEmail());
        }

        if (userUpdateRequest.getName() != null) {
            savedUser.setName(userUpdateRequest.getName());
        }
        return userRepository.updateUser(savedUser);
    }

    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }
}
