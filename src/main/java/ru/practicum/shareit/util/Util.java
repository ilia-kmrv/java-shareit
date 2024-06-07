package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }
}
