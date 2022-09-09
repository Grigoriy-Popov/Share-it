package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {

    public static Booking fromInputDto(InputBookingDto inputBookingDto) {
        return Booking.builder()
                .start(inputBookingDto.getStart())
                .end(inputBookingDto.getEnd())
                .build();
    }

    public static OutputBookingDto toOutputDto(Booking booking) {
        return OutputBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toDto(booking.getItem()))
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static ForItemBookingDto toItemBookingDto(Booking booking) {
        return new ForItemBookingDto(booking.getId(), booking.getBooker().getId(),
                booking.getStart(), booking.getEnd());
    }

    public static List<OutputBookingDto> toOutputDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toOutputDto)
                .collect(Collectors.toList());
    }
}
