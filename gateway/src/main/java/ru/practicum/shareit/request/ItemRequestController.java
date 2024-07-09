package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.Header;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> postItemRequest(@Valid @RequestBody ItemRequestDto dto, @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на добавление запроса на вещь от пользователя с id={}", userId);
        return requestClient.postItemRequest(dto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUser(@RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на просмотр запросов от пользователя с id={}", userId);
        return requestClient.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size,
                                                 @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Просмотр запросов от {} размером {} от пользователя с id={}", from, size, userId);
        return requestClient.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                             @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Просмотр запроса по id={} от пользователя id={}", requestId, userId);
        return requestClient.getRequest(requestId, userId);
    }
}
