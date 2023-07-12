package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;

import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto bookItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @RequestBody InputBookingDto inputBookingDto) {
        Booking booking = BookingMapper.fromInputDto(inputBookingDto);
        Long itemId = inputBookingDto.getItemId();
        return BookingMapper.toOutputDto(bookingService.createBooking(booking, userId, itemId));
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto approveBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam Boolean approved) {
        return BookingMapper.toOutputDto(bookingService.approveBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getBookingById(@RequestHeader(USER_ID_HEADER) Long userId,
                                           @PathVariable Long bookingId) {
        return BookingMapper.toOutputDto(bookingService.getBookingByIdByUser(userId, bookingId));
    }

    @GetMapping
    public List<OutputBookingDto> getAllUserBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam BookingState state,
                                                     @RequestParam int from,
                                                     @RequestParam int size) {
        return BookingMapper.toOutputDto(bookingService.getAllUserBookings(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> getAllUserItemsBookings(@RequestHeader(USER_ID_HEADER) Long userId,
                                                          @RequestParam BookingState state,
                                                          @RequestParam int from,
                                                          @RequestParam int size) {
        return BookingMapper.toOutputDto(bookingService.getAllUserItemsBookings(userId, state, from, size));
    }
}
