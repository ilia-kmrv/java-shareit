package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(groups = OnCreate.class, message = "Название предмета не может быть пустым")
    private String name;

    @NotBlank(groups = OnCreate.class, message = "Описание не может быть пустым")
    private String description;

    @NotNull(groups = OnCreate.class, message = "Доступность предмета не может быт null")
    private Boolean available;

    private Long ownerId;

    private Long requestId;

}
