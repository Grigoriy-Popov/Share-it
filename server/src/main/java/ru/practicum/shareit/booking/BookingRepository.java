package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId, Pageable page);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long userId,
                                                                                 LocalDateTime endDateTime,
                                                                                 LocalDateTime startDateTime,
                                                                                 Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime endDateTime,
                                                                  Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime startDateTime,
                                                                   Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Long userId, LocalDateTime startDateTime,
            BookingStatus bookingStatus, Pageable page);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable page);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :userId " +
            "ORDER BY b.start DESC")
    List<Booking> getAllUsersItemsBookings(Long userId, Pageable page);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :userId " +
            "AND :nowTime BETWEEN b.start AND b.end ORDER BY b.start DESC")
    List<Booking> getCurrentUsersItemsBookings(Long userId, LocalDateTime nowTime, Pageable page);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :userId " +
            "AND b.end < :nowTime ORDER BY b.start DESC")
    List<Booking> getPastUsersItemsBookings(Long userId, LocalDateTime nowTime, Pageable page);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :userId " +
            "AND b.start > :nowTime ORDER BY b.start DESC")
    List<Booking> getFutureUsersItemsBookings(Long userId, LocalDateTime nowTime, Pageable page);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :userId " +
            "AND b.start > :nowTime AND b.status = 'WAITING' ORDER BY b.start DESC")
    List<Booking> getWaitingUsersItemsBookings(Long userId, LocalDateTime nowTime, Pageable page);

    @Query("SELECT b FROM Booking b JOIN Item i ON b.item.id = i.id WHERE i.owner.id = :userId " +
            "AND b.status = 'REJECTED' ORDER BY b.start DESC")
    List<Booking> getRejectedUsersItemsBookings(Long userId, Pageable page);

    @Query(value = "SELECT * FROM bookings b JOIN items i ON i.id = b.item_id "
            + "WHERE b.item_id = :itemId AND b.end_time < :nowTime ORDER BY b.end_time ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getLastItemBooking(Long itemId, LocalDateTime nowTime);

    @Query(value = "SELECT * FROM bookings b JOIN items i ON i.id = b.item_id "
            + "WHERE b.item_id = :itemId AND b.start_time > :nowTime ORDER BY b.start_time ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getNextItemBooking(Long itemId, LocalDateTime nowTime);

    List<Booking> findAllByItemAndBookerIdAndStatusAndEndBefore(Item item, Long userId, BookingStatus bookingStatus,
                                                                LocalDateTime nowTime);
}
