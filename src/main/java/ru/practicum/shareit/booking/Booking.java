package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@AllArgsConstructor
public class Booking {
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    @NotNull
    private BookingStatus status;
}
