package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "Название предмета не может быть пустым")
    private String name;

    private String description;

    @NotNull(message = "Доступность предмета не может быт null")
    private Boolean isAvailable;

    @NotNull(message = "У предмета не может не быть хозяина")
    private User owner;

    private Long request;

    public ItemDto(String name, String description, Boolean isAvailable, Long requestId) {
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.request = requestId;
    }
}
