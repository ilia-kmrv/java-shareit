package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.exception.ResourceNotFoundException;
import ru.practicum.shareit.exception.ResourceValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    private final UserService userService;
    private final BookingService bookingService;

    @Override
    @Transactional
    public Item addItem(Item item, Long ownerId) {
        log.debug("Обработка запроса на создание вещи");
        userService.getUser(ownerId);
        Item itemWithOwner = item.toBuilder().ownerId(ownerId).build();
        return itemRepository.save(itemWithOwner);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerItemDto getItem(Long itemId, Long userId) {
        log.debug("Обработка запроса на получение вещи c id={}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        User user = userService.getUser(userId);

        List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        if (!item.getOwnerId().equals(user.getId())) {
            return ItemMapper.toOwnerItemDto(item, null, null, comments);
        }

        return ItemMapper.toOwnerItemDto(item,
                bookingService.getLastBooking(itemId),
                bookingService.getNextBooking(itemId),
                comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<OwnerItemDto> getAllItems(Long ownerId, Integer from, Integer size) {
        log.debug("Обработка запроса на получение всех вещей пользователя  id={}", ownerId);
        userService.getUser(ownerId);
        Pageable page = Util.page(from, size);
        return itemRepository.findByOwnerId(ownerId, page).stream()
                .map(i -> ItemMapper.toOwnerItemDto(i,
                        bookingService.getLastBooking(i.getId()),
                        bookingService.getNextBooking(i.getId()),
                        Collections.emptyList()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Long itemId, Long ownerId) {
        log.debug("Обработка запроса на обновление вещи c id={}", item.getId());
        userService.getUser(ownerId);
        Item itemInDb = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));

        if (!itemInDb.getOwnerId().equals(ownerId)) {
            throw new PermissionDeniedException("Вещь не принадлежит данному пользователю");
        }

        Item itemForUpdate = itemInDb.toBuilder()
                .name(Util.isBlank(item.getName()) ? itemInDb.getName() : item.getName())
                .description(Util.isBlank(item.getDescription()) ? itemInDb.getDescription() : item.getDescription())
                .available(item.getAvailable() == null ? itemInDb.getAvailable() : item.getAvailable())
                .build();
        return itemRepository.save(itemForUpdate);
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId, Long ownerId) {
        log.debug("Обработка запроса на удаление вещи c id={}", itemId);
        userService.getUser(ownerId);
        Item itemInDb = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        if (itemInDb.getOwnerId().equals(ownerId)) {
            itemRepository.deleteById(itemId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Item> searchItems(String text, Integer from, Integer size) {
        log.debug("Обработка запроса на поиск доступных вещей по тексту text={}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable page = Util.page(from, size);
        return itemRepository.search(text, page);
    }

    @Override
    @Transactional
    public CommentDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        log.debug("Обработка запроса на добавление комментария на вещь={} от пользователя={}", itemId, userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Вещь с id=%d не найдена", itemId)));
        User user = userService.getUser(userId);

        if (item.getOwnerId().equals(user.getId())
                || bookingService.getPastUserBookings(itemId, userId).isEmpty()) {
            throw new ResourceValidationException("Комментарий может оставлять только пользователь завершивший аренду");
        }

        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, item, user)));
    }
}
