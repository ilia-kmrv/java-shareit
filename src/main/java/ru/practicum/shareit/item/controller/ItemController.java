package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Header;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @Validated(OnCreate.class)
    public ItemDto postItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на добавление вещи для пользователя с id={}", ownerId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.addItem(item, ownerId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.info("Получен запрос на просмотр вещи c id={}", itemId);
        return ItemMapper.toItemDto(itemService.getItem(itemId));
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на просмотр всех вещей пользователя id={}", ownerId);
        return itemService.getAllItems(ownerId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PatchMapping("/{itemId}")
    @Validated(OnUpdate.class)
    public ItemDto patchItem(@Valid @RequestBody ItemDto itemDto,
                             @PathVariable Long itemId,
                             @RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на обновление вещи c id={} пользователем с id={}", itemId, ownerId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.updateItem(item, itemId, ownerId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId, @RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на удаление вещи c id={} пользователем c id={}", itemId, ownerId);
        itemService.deleteItem(itemId, ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен запрос на поиск доступных вещей по тексту text={}", text);
        return itemService.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

}
