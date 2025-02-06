package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateRequestDtoServer {
    private String description;
}
