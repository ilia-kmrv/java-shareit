package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@UtilityClass
public class Util {
    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static Pageable page(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }
}

