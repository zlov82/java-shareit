package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateRequestDtoServer;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;

    public ItemRequest createRequest(Long userId, CreateRequestDtoServer requestDto) {
        User user = userService.getById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(user, requestDto);
        return repository.save(itemRequest);
    }

    public List<ItemRequest> getUserRequests(Long userId) {
        User user = userService.getById(userId);
        List<ItemRequest> itemRequests = repository.findAllByRequestorOrderByCreatedDesc(user);
        List<ItemRequest> resultItemRequests = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            resultItemRequests.add(this.getRequestById(itemRequest.getId()));
        }
        return resultItemRequests;
    }

    public List<ItemRequest> getAnotherUserRequest(Long userId) {
        User user = userService.getById(userId);
        return repository.findAllByAnotherUser(user);
    }

    public ItemRequest getById(long requestId) {
        return repository.findById(requestId).orElseThrow(() -> new NotFoundException("Не найден запрос на вещь"));
    }

    public ItemRequest getRequestById(long requestId) {
        ItemRequest itemRequest = this.getById(requestId);
        List<Item> itemList = itemRepository.findAllByItemRequest(itemRequest);
        for (Item item : itemList) {
            itemRequest.getItems().add(item);
        }
        return itemRequest;
    }
}
