package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ItemIsBookedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(Booking booking, Long userId, Long itemId) {
        User user = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id %d not found", itemId)));
        if (!item.getAvailable()) {
            throw new ItemIsBookedException("Item is already booked");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner can't book his item");
        }
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = getBookingById(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserIsNotOwnerException("Only owner of the item can approve booking");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new ItemIsBookedException("You can change status only for waiting bookings");
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingByIdByUser(Long userId, Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return booking;
        } else {
            throw new UserIsNotOwnerException("Only owner of the item or booker can view information about booking");
        }
    }

    @Override
    public List<Booking> getAllUserBookings(Long userId, BookingState state, Integer from, Integer size) {
        userService.checkExistenceById(userId);
        var now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, page);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now,
                        now, page);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, page);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, page);
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId,
                        now, BookingStatus.WAITING, page);
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(userId,
                        BookingStatus.REJECTED, page);
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }

    @Override
    public List<Booking> getAllUserItemsBookings(Long userId, BookingState state, Integer from, Integer size) {
        userService.getUserById(userId);
        var now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                return bookingRepository.getAllUsersItemsBookings(userId, page);
            case CURRENT:
                return bookingRepository.getCurrentUsersItemsBookings(userId, now, page);
            case PAST:
                return bookingRepository.getPastUsersItemsBookings(userId, now, page);
            case FUTURE:
                return bookingRepository.getFutureUsersItemsBookings(userId, now, BookingStatus.APPROVED, page);
            case WAITING:
                return bookingRepository.getWaitingUsersItemsBookings(userId, now, BookingStatus.WAITING, page);
            case REJECTED:
                return bookingRepository.getRejectedUsersItemsBookings(userId, BookingStatus.REJECTED, page);
            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }

    private Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id %d not found", bookingId)));
    }
}
