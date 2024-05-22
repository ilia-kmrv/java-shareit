package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

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

    @NotNull(message = "У предмета не может не быть владельца")
    private Long ownerId;

    private Long requestId;

}
