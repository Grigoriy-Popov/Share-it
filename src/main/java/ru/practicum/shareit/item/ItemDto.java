package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ForItemBookingDto;
import ru.practicum.shareit.item.comments.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank (message = "Name can't be empty")
    private String name;
    @NotBlank (message = "Description can't be empty")
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private ForItemBookingDto lastBooking;
    private ForItemBookingDto nextBooking;
    private List<CommentDto> comments;
}
