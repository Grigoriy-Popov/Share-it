package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking addBooking(Booking booking, Long userId);

    Booking approve(Long userId, Long bookingId, Boolean isApproved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getAllUsersBookings(Long userId, State state);

    List<Booking> getAllUsersItemsBookings(Long userId, State state);
}
