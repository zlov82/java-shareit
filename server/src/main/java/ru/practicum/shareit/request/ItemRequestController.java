package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final Logger log = LoggerFactory.getLogger(ItemRequestController.class);
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody CreateItemRequestDto requestDto) {
        ItemRequest itemRequest = service.addNewRequest(userId,requestDto);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        //С данными о вещах
        List<ItemRequest> itemRequests = service.getUserRequests(userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAnotherUserRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemRequest> itemRequests = service.getAnotherUserRequest(userId);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getById(@PathVariable long requestId) {
        // с данными о вещах
        ItemRequest itemRequest = service.getRequestById(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
}
