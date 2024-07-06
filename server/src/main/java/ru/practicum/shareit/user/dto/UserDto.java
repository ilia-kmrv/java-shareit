package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя пользователя не может быть пустым", groups = OnCreate.class)
    private String name;

    @NotNull(groups = OnCreate.class)
    @Email(message = "Указан некорректный Email")
    private String email;

}
