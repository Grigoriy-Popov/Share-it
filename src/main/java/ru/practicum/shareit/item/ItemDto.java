package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.ForItemBookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;
    @NotBlank (message = "Name can't be empty")
    private String name;
    @NotBlank (message = "Description can't be empty")
    private String description;
    @NotNull
    private Boolean available;
    private ForItemBookingDto lastBooking;
    private ForItemBookingDto nextBooking;
    private List<CommentDto> comments;
}
