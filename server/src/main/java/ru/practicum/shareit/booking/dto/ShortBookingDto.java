package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ShortBookingDto {
    private Long id;

    private Long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;
}
