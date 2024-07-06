package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {

    private Long id;

    @NotBlank(message = "Описание запрашиваемой вещи не может быть пустым")
    private String description;

    private LocalDateTime created;

}
