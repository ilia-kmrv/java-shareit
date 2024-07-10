package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Header;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService  itemRequestService;

    @PostMapping
    public ItemRequestDto postItemRequest(@RequestBody ItemRequestDto dto, @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на добавление запроса на вещь от пользователя с id={}", userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.addItemRequest(dto, userId));
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getRequestsByUser(@RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на просмотр запросов от пользователя с id={}", userId);
        return itemRequestService.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllRequests(@RequestParam Integer from,
                                                        @RequestParam Integer size,
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
