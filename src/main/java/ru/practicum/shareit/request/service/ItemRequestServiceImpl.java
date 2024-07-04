package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        log.debug("Обработка добавления запроса от пользователя id={}", userId);
        User user = userService.getUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return requestRepository.save(itemRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoWithItems> getAllRequests(Integer from, Integer size, Long userId) {
        log.debug("Обработка получения запросов кроме запросов пользователя id={}", userId);
        userService.getUser(userId);
        Pageable page = Util.page(from, size);
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, page);
        Set<Long> requestIds = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestsWithItems = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        return itemRequestList.stream()
                .map(ir -> ItemRequestMapper.toItemRequestDtoWithItems(ir,
                        requestsWithItems.getOrDefault(ir.getId(), List.of()).stream()
                                .map(ItemMapper::toItemDtoForRequest)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoWithItems> getRequestsByUserId(Long userId) {
        log.debug("Получение всех запросов с вещами для пользователя id={}", userId);
        userService.getUser(userId);
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        Set<Long> requestIds = itemRequestList.stream().map(ItemRequest::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestsWithItems = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        return itemRequestList.stream()
                .map(ir -> ItemRequestMapper.toItemRequestDtoWithItems(ir,
                        requestsWithItems.getOrDefault(ir.getId(), List.of()).stream()
                                .map(ItemMapper::toItemDtoForRequest)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithItems getRequest(Long requestId, Long userId) {
        log.debug("Получение запроса с id={}", requestId);
        userService.getUser(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Запроса с id=%d нет в базе", requestId)));
        List<Item> itemList = itemRepository.findAllByRequestIdIn(Set.of(requestId));

        return ItemRequestMapper.toItemRequestDtoWithItems(request,
                itemList.stream().map(ItemMapper::toItemDtoForRequest).collect(Collectors.toList()));
    }


}
