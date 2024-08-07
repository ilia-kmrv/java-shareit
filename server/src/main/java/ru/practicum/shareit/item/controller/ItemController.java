package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Header;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto postItem(@RequestBody ItemDto itemDto, @RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на добавление вещи для пользователя с id={}", ownerId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.addItem(item, ownerId));
    }

    @GetMapping("/{itemId}")
    public OwnerItemDto getItem(@PathVariable Long itemId, @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на просмотр вещи c id={}", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public Collection<OwnerItemDto> getAllItems(@RequestHeader(Header.USER_ID) Long ownerId,
                                                @RequestParam Integer from,
                                                @RequestParam Integer size) {
        log.info("Получен запрос на просмотр всех вещей пользователя id={} c {} размер {}", ownerId, from, size);
        return itemService.getAllItems(ownerId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestBody ItemDto itemDto,
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
    public Collection<ItemDto> searchItems(@RequestParam String text,
                                           @RequestParam Integer from,
                                           @RequestParam Integer size) {
        log.info("Получен запрос на поиск доступных вещей по тексту text={} c {} размером {}", text, from, size);
        return itemService.searchItems(text, from, size).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestBody CommentDto commentDto,
                                  @PathVariable Long itemId,
                                  @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на добавление комментария к вещи={} от пользователя={}", itemId, userId);
        return itemService.addComment(commentDto, itemId, userId);
    }

}
