package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String email;
}
