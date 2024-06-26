package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private Long id;

    @NotBlank
    private String text;

    private ItemDto item;

    private String authorName;

    private LocalDateTime created;
}
