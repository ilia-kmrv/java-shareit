package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoWithItems {
    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDtoForRequest> items;
}
