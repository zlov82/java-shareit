package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateRequest;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        return UserMapper.userToDto(userService.getById(userId));
    }

    @PostMapping()
    public UserDto addUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        return UserMapper.userToDto(userService.addUser(userCreateRequest));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody UserUpdateRequest userUpdateRequest) {
        return UserMapper.userToDto(userService.updateUserById(userId, userUpdateRequest));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }

}
