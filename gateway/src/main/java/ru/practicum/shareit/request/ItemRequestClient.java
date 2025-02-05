package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.CreateRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(CreateRequestDto requestInputDto, Long userId) {
        return post("", userId, requestInputDto);
    }

    public ResponseEntity<Object> getRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getRequest(Long requestId, Long userId) {
        return get("/" + requestId, userId);
    }
}
