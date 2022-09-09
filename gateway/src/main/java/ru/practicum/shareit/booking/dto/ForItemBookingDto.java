package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ForItemBookingDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
