package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class Util {
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

}

