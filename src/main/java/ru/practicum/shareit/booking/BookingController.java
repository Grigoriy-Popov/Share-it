package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public OutputBookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @Valid @RequestBody InputBookingDto inputBookingDto) {
        Booking booking = bookingMapper.fromInputDto(inputBookingDto);
        return bookingMapper.toOutputDto(bookingService.addBooking(booking, userId));
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam Boolean approved) {
        return bookingMapper.toOutputDto(bookingService.approve(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        return bookingMapper.toOutputDto(bookingService.getBooking(userId, bookingId));
    }

    /*Не знаю как лучше - передавать в метод ниже сразу enum и обрабатывать MethodArgumentTypeMismatchException
    в ErrorHandler или передавать строку и проверять её преобразование в enum*/
//    @GetMapping
//    public List<Booking> getAllUsersBookings(
//            @RequestHeader("X-Sharer-User-Id") Long userId,
//            @RequestParam(defaultValue = "ALL") String state) {
//        State enumState;
//        try {
//            enumState = State.valueOf(state);
//        } catch (IllegalArgumentException e) {
//            throw new IncorrectStateException("Unknown state: " + state);
//        }
//        return bookingService.getAllUsersBookings(userId, enumState);
//    }

    @GetMapping
    public List<OutputBookingDto> getAllUsersBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") State state) { // при передаче некорректного параметра state идёт
                                                        // обработка MethodArgumentTypeMismatchException в ErrorHandler
        return bookingMapper.toOutputDtoList(bookingService.getAllUsersBookings(userId, state));
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> getAllUsersItemsBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") State state) { // при передаче некорректного параметра state идёт
                                                        // обработка MethodArgumentTypeMismatchException в ErrorHandler
        return bookingMapper.toOutputDtoList(bookingService.getAllUsersItemsBookings(userId, state));
    }
}
