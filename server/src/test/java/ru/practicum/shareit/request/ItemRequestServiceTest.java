package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class ItemRequestServiceTest {
    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder()
                .name("Requester")
                .email("requester@mail.com")
                .build());

        user2 = userRepository.save(User.builder()
                .name("Another User")
                .email("another@mail.com")
                .build());
    }

    @Test
    void whenCreateRequest_thenReturnRequestOutputDto() {
        CreateItemRequestDto input = CreateItemRequestDto.builder()
                .description("Нужна дрель")
                .build();

        ItemRequest outputDto = requestService.createRequest(user1.getId(), input);

        assertNotNull(outputDto);
        assertNotNull(outputDto.getId());
        assertEquals("Нужна дрель", outputDto.getDescription());
        assertNotNull(outputDto.getItems());
        assertTrue(outputDto.getItems().isEmpty());
        assertNotNull(outputDto.getCreated());
    }

    @Test
    void whenCreateRequestWithNonExistingUser_thenThrowNotFoundException() {
        CreateItemRequestDto inputDto = CreateItemRequestDto.builder()
                .description("Нужна отвертка")
                .build();

        long nonExistingUserId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.createRequest(nonExistingUserId, inputDto));
    }

    @Test
    void whenGetRequestsForUserWithoutRequests_thenReturnEmptyList() {
        List<ItemRequest> requests = requestService.getUserRequests(user2.getId());
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void whenGetAllRequests_thenReturnListOfRequests() {
        CreateItemRequestDto inputDto1 = CreateItemRequestDto.builder()
                .description("Запрос 1")
                .build();
        ItemRequest req1 = requestService.createRequest(user1.getId(), inputDto1);

        CreateItemRequestDto inputDto2 = CreateItemRequestDto.builder()
                .description("Запрос 2")
                .build();
        ItemRequest req2 = requestService.createRequest(user1.getId(), inputDto2);

        List<ItemRequest> requests = requestService.getUserRequests(user1.getId());
        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Запрос 1")));
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Запрос 2")));
    }

    @Test
    void whenGetRequestById_thenReturnRequestOutputDto() {
        CreateItemRequestDto inputDto = CreateItemRequestDto.builder()
                .description("Запрос для получения")
                .build();
        ItemRequest created = requestService.createRequest(user1.getId(), inputDto);

        ItemRequest request = requestRepository.findById(created.getId()).orElseThrow();
        Item item = Item.builder()
                .name("Молоток")
                .description("Качественный молоток")
                .available(true)
                .itemRequest(request)
                .owner(user2)
                .build();
        itemRepository.save(item);

        ItemRequest outputDto = requestService.getRequestById(created.getId());
        assertNotNull(outputDto);
        assertEquals(created.getId(), outputDto.getId());
        assertEquals("Запрос для получения", outputDto.getDescription());
        assertNotNull(outputDto.getItems());
        assertFalse(outputDto.getItems().isEmpty());
        assertTrue(outputDto.getItems().stream().anyMatch(i -> i.getName().equals("Молоток")));
    }

    @Test
    void whenGetNonExistingRequestById_thenThrowNotFoundException() {
        long nonExistingRequestId = 999L;
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(nonExistingRequestId));
    }

    @Test
    void getAnotherUserRequest() {
        CreateItemRequestDto request1 = CreateItemRequestDto.builder()
                .description("Нужно для пользователя " + user1.getId())
                .build();

        CreateItemRequestDto request2 = CreateItemRequestDto.builder()
                .description("Нужно для пользователя " + user2.getId())
                .build();

        ItemRequest itemRequest1 = requestService.createRequest(user1.getId(), request1);
        ItemRequest itemRequest2 = requestService.createRequest(user2.getId(), request2);

        List<ItemRequest> itemRequests = requestService.getAnotherUserRequest(user1.getId());
        assertNotNull(itemRequests);
        assertEquals(1, itemRequests.size());
        assertEquals(user2.getId(), itemRequests.get(0).getRequestor().getId());
    }
}
