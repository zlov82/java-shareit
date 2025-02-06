package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class UpdateItemRequestDto {
    private String name;
    private String description;
    private Boolean available;
}
