package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
@Builder
public class InputBookingDto {
    private Long id;
    @Future
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
