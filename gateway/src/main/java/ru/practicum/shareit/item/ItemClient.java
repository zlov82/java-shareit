package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> getOwnerItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getOwnerItem(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", parameters);
    }

    public ResponseEntity<Object> editItem(Long id, UpdateItemRequestDto item, Long userId) {
        return patch("/" + id, userId, item);
    }

    public ResponseEntity<Object> createItem(CreateItemRequestDto item, Long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> createComment(CreateCommentRequestDto commentRequestDto, Long userId, Long itemId) {
        return post("/%d/comment".formatted(itemId), userId, commentRequestDto);
    }
}
