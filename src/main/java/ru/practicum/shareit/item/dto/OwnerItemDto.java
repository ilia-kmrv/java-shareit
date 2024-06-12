package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class OwnerItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private ShortBookingDto lastBooking;

    private ShortBookingDto nextBooking;

    private List<CommentDto> comments;

}
