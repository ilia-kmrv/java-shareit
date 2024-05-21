package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String name;

    @Email(message = "Указан некорректный Email")
    private String email;
}
