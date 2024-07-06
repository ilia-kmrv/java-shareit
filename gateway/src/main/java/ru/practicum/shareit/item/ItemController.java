package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.util.Header;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> postItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на добавление вещи для пользователя с id={}", ownerId);
        return itemClient.postItem(itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId, @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на просмотр вещи c id={}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(Header.USER_ID) Long ownerId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на просмотр всех вещей пользователя id={} c {} размер {}", ownerId, from, size);
        return itemClient.getAllItems(ownerId, from, size);
    }

    @PatchMapping("/{itemId}")
    @Validated(OnUpdate.class)
    public ResponseEntity<Object> patchItem(@Valid @RequestBody ItemDto itemDto,
                                            @PathVariable Long itemId,
                                            @RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на обновление вещи c id={} пользователем с id={}", itemId, ownerId);
        return itemClient.patchItem(itemDto, itemId, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Long itemId, @RequestHeader(Header.USER_ID) Long ownerId) {
        log.info("Получен запрос на удаление вещи c id={} пользователем c id={}", itemId, ownerId);
        itemClient.deleteItem(itemId, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader(Header.USER_ID) Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получен запрос на поиск доступных вещей по тексту text={} c {} размером {}", text, from, size);
        return itemClient.searchItems(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@Valid @RequestBody CommentDto commentDto,
                                              @PathVariable Long itemId,
                                              @RequestHeader(Header.USER_ID) Long userId) {
        log.info("Получен запрос на добавление комментария к вещи={} от пользователя={}", itemId, userId);
        return itemClient.postComment(commentDto, itemId, userId);
    }

}
