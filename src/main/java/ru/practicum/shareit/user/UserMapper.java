package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static UserDto userToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserCreateRequest userCreateRequest) {
        return User.builder()
                .name(userCreateRequest.getName())
                .email(userCreateRequest.getEmail())
                .build();
    }
}
