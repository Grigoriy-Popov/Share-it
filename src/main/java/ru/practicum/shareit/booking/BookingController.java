package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto createBooking(@RequestHeader(USER_ID_HEADER) Long userId,
                                       @Valid @RequestBody InputBookingDto inputBookingDto) {
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
        return BookingMapper.toOutputDto(bookingService.getBookingById(userId, bookingId));
    }

    // при передаче некорректного параметра state идёт обработка MethodArgumentTypeMismatchException в ErrorHandler
    @GetMapping
    public List<OutputBookingDto> getAllUserBookings(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") State state,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return BookingMapper.toOutputDtoList(bookingService.getAllUserBookings(userId, state, from, size));
    }

    // при передаче некорректного параметра state идёт обработка MethodArgumentTypeMismatchException в ErrorHandler
    @GetMapping("/owner")
    public List<OutputBookingDto> getAllUserItemsBookings(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") State state,
            @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return BookingMapper.toOutputDtoList(bookingService.getAllUserItemsBookings(userId, state, from, size));
    }
}
