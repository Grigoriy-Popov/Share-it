package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ItemIsBookedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;

    User user = new User(1L, "testUser", "test@email.com");
    Item availableItem = Item.builder().id(1L).name("testItem").description("testDescr").available(true).owner(user).build();
    Item unavailableItem = Item.builder().id(1L).name("testItem").description("testDescr").available(false).owner(user).build();
    Booking booking = Booking.builder().booker(user).item(availableItem).status(BookingStatus.APPROVED).build();

    @Test
    public void createBooking_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(booking,
                1L, 1L));
        assertThat(e.getMessage(), equalTo("User with id 1 not found"));
    }

    @Test
    public void createBooking_shouldThrowExceptionWhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(booking,
                1L, 1L));
        assertThat(e.getMessage(), equalTo("Item with id 1 not found"));
    }

    @Test
    public void createBooking_shouldThrowExceptionWhenItemIsBooked() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(unavailableItem));

        Exception e = Assertions.assertThrows(ItemIsBookedException.class, () -> bookingService.createBooking(booking,
                1L, 1L));
        assertThat(e.getMessage(), equalTo("Item is already booked"));
    }

    @Test
    public void createBooking_shouldThrowExceptionWhenOwnerTryToBookHisItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(availableItem));

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> bookingService.createBooking(booking,
                1L, 1L));
        assertThat(e.getMessage(), equalTo("Owner can't book his item"));
    }

    @Test
    public void approveBooking_shouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L,
                1L, true));
        assertThat(e.getMessage(), equalTo("Booking with id 1 not found"));
    }

    @Test
    public void approveBooking_shouldThrowExceptionWhenNotOwnerTryToApproveItem() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Exception e = Assertions.assertThrows(UserIsNotOwnerException.class, () -> bookingService.approveBooking(2L,
                1L, true));
        assertThat(e.getMessage(), equalTo("Only owner of the item can approve booking"));
    }

    @Test
    public void approveBooking_shouldThrowExceptionWhenOwnerTryToApproveBookingWithNowWaitingStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Exception e = Assertions.assertThrows(ItemIsBookedException.class, () -> bookingService.approveBooking(1L,
                1L, true));
        assertThat(e.getMessage(), equalTo("You can change status only for waiting bookings"));
    }

    @Test
    public void getBookingById_shouldThrowExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L,
                1L));
        assertThat(e.getMessage(), equalTo("Booking with id 1 not found"));
    }

    @Test
    public void getBookingById_shouldThrowExceptionWhenNotOwnerOrBookerTryToGetBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Exception e = Assertions.assertThrows(UserIsNotOwnerException.class, () -> bookingService.getBookingById(3L,
                1L));
        assertThat(e.getMessage(), equalTo("Only owner of the item or booker can view information about booking"));
    }

    @Test
    public void getAllUserBookings_shouldThrowExceptionWhenBookingNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> bookingService.getAllUserBookings(1L,
                BookingState.ALL, 0, 10));
        assertThat(e.getMessage(), equalTo("User with id 1 not found"));
    }

    @Test
    public void getAllUserItemsBookings_shouldThrowExceptionWhenBookingNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllUserItemsBookings(1L, BookingState.ALL, 0, 10));
        assertThat(e.getMessage(), equalTo("User with id 1 not found"));
    }
}