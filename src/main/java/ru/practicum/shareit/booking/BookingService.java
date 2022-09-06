package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, Long userId, Long itemId);

    Booking approveBooking(Long userId, Long bookingId, Boolean isApproved);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllUserBookings(Long userId, State state, Integer from, Integer size);

    List<Booking> getAllUserItemsBookings(Long userId, State state, Integer from, Integer size);
}
