package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Header;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService  itemRequestService;

    @PostMapping
    @Validated(OnCreate.class)
    public ItemRequestDto postItemRequest(@Valid @RequestBody ItemRequestDto dto, @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на добавление запроса на вещь от пользователя с id={}", userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addItemRequest(dto, userId));
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getRequestsByUser(@RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на просмотр запросов от пользователя с id={}", userId);
        return itemRequestService.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllRequests(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                        @RequestParam(defaultValue = "10") @Positive Integer size,
                                                        @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Просмотр запросов от {} размером {} от пользователя с id={}", from, size, userId);
        return itemRequestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getRequest(@PathVariable Long requestId,
                                              @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Просмотр запроса по id={} от пользователя id={}", requestId, userId);
        return itemRequestService.getRequest(requestId, userId);
    }
}
