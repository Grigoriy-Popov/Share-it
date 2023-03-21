package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ForItemBookingDto;
import ru.practicum.shareit.item.comments.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private ForItemBookingDto lastBooking;

    private ForItemBookingDto nextBooking;

    private List<CommentDto> comments;
}
