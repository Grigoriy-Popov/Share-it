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

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime endDateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long userId,
                                                                   LocalDateTime startDateTime, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Long userId,
                                                                              LocalDateTime startDateTime,
                                                                              BookingStatus bookingStatus, Pageable page);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long userId, BookingStatus status, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> getAllUsersItemsBookings(Long userId, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and ?2 between b.start and b.end order by b.start desc")
    List<Booking> getCurrentUsersItemsBookings(Long userId, LocalDateTime nowTime, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.end < ?2 order by b.start desc")
    List<Booking> getPastUsersItemsBookings(Long userId, LocalDateTime nowTime, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.start > ?2 order by b.start desc")
    List<Booking> getFutureUsersItemsBookings(Long userId, LocalDateTime nowTime, BookingStatus status, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.start > ?2 and b.status = ?3 order by b.start desc")
    List<Booking> getWaitingUsersItemsBookings(Long userId, LocalDateTime nowTime, BookingStatus status, Pageable page);

    @Query("select b from Booking b Inner join Item i on b.item.id = i.id where i.owner.id = ?1 " +
            "and b.status = ?2 order by b.start desc")
    List<Booking> getRejectedUsersItemsBookings(Long userId, BookingStatus status, Pageable page);


//    Для сортировки и фильтрации на стороне БД
    @Query(value = "SELECT * FROM bookings b INNER JOIN items i on i.id = b.item_id "
            + "WHERE b.item_id = ?1 AND b.end_time < ?2 ORDER BY b.end_time ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getLastItemBooking(Long itemId, LocalDateTime nowTime);

    @Query(value = "SELECT * FROM bookings b INNER JOIN items i on i.id = b.item_id "
            + "WHERE b.item_id = ?1 and b.start_time > ?2 ORDER BY b.start_time ASC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> getNextItemBooking(Long itemId, LocalDateTime nowTime);

    List<Booking> findAllByItemAndBookerIdAndStatusAndEndBefore(Item item, Long userId, BookingStatus bookingStatus,
                                                                LocalDateTime nowTime);
}
