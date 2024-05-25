package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {
    private Long id;

    private String name;

    private String email;
}
