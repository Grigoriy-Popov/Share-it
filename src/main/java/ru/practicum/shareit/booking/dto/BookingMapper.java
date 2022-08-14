package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final ItemRepository itemRepository;

    public Booking fromInputDto(InputBookingDto inputBookingDto) {
        return Booking.builder()
                .start(inputBookingDto.getStart())
                .end(inputBookingDto.getEnd())
                .item(itemRepository.findById(inputBookingDto.getItemId())
                        .orElseThrow(() -> new NotFoundException
                                (String.format("Item with id %d not found", inputBookingDto.getItemId()))))
                .build();
    }

    public OutputBookingDto toOutputDto(Booking booking) {
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

    public List<OutputBookingDto> toOutputDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toOutputDto)
                .collect(Collectors.toList());
    }
}
