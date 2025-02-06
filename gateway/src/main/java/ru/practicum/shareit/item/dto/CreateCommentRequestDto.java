package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentRequestDto {
    @NotNull
    private String text;
}
