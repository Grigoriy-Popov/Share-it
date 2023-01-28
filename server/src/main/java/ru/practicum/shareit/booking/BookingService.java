package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking, Long userId, Long itemId);

    Booking approveBooking(Long userId, Long bookingId, Boolean isApproved);

    Booking getBookingByIdByUser(Long userId, Long bookingId);

    List<Booking> getAllUserBookings(Long userId, BookingState state, int from, int size);

    List<Booking> getAllUserItemsBookings(Long userId, BookingState state, int from, int size);

}
