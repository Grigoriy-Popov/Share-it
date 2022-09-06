package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    User ownerOfItem = new User(null, "testUser", "test@email.com");
    User booker = new User(null, "testUser2", "test2@email.com");
    ItemDto itemDtoToCreate = ItemDto.builder().name("testItem").description("testDescription").available(true).build();
    Booking bookingToCreate = Booking.builder().start(LocalDateTime.of(2020, 12, 12, 12, 12, 12))
            .end(LocalDateTime.of(2021, 12, 12, 12, 12, 12)).build();

    @Test
    void createBooking() {
        userService.createUser(ownerOfItem);
        User createdBooker = userService.createUser(booker);
        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);

        Booking createdBooking = bookingService.createBooking(bookingToCreate, 2L, 1L);

        assertThat(createdBooking.getId(), equalTo(1L));
        assertThat(createdBooking.getStart(), equalTo(bookingToCreate.getStart()));
        assertThat(createdBooking.getEnd(), equalTo(bookingToCreate.getEnd()));
        assertThat(createdBooking.getBooker(), equalTo(createdBooker));
        assertThat(createdBooking.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(createdBooking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void approveBooking_approve() {
        userService.createUser(ownerOfItem);
        User createdBooker = userService.createUser(booker);
        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);
        bookingService.createBooking(bookingToCreate, 2L, 1L);

        Booking approvedBooking = bookingService.approveBooking(1L, 1L, true);

        assertThat(approvedBooking.getId(), equalTo(1L));
        assertThat(approvedBooking.getStart(), equalTo(bookingToCreate.getStart()));
        assertThat(approvedBooking.getEnd(), equalTo(bookingToCreate.getEnd()));
        assertThat(approvedBooking.getBooker(), equalTo(createdBooker));
        assertThat(approvedBooking.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(approvedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approveBooking_reject() {
        userService.createUser(ownerOfItem);
        User createdBooker = userService.createUser(booker);
        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);
        bookingService.createBooking(bookingToCreate, 2L, 1L);

        Booking approvedBooking = bookingService.approveBooking(1L, 1L, false);

        assertThat(approvedBooking.getId(), equalTo(1L));
        assertThat(approvedBooking.getStart(), equalTo(bookingToCreate.getStart()));
        assertThat(approvedBooking.getEnd(), equalTo(bookingToCreate.getEnd()));
        assertThat(approvedBooking.getBooker(), equalTo(createdBooker));
        assertThat(approvedBooking.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(approvedBooking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getBookingById() {
        userService.createUser(ownerOfItem);
        User createdBooker = userService.createUser(booker);
        ItemDto itemDto = itemService.createItem(itemDtoToCreate, 1L);
        bookingService.createBooking(bookingToCreate, 2L, 1L);

        Booking returnedBooking = bookingService.getBookingById(2L, 1L);

        assertThat(returnedBooking.getId(), equalTo(1L));
        assertThat(returnedBooking.getStart(), equalTo(bookingToCreate.getStart()));
        assertThat(returnedBooking.getEnd(), equalTo(bookingToCreate.getEnd()));
        assertThat(returnedBooking.getBooker(), equalTo(createdBooker));
        assertThat(returnedBooking.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(returnedBooking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getAllUserBookings() {
        userService.createUser(ownerOfItem);
        userService.createUser(booker);
        itemService.createItem(itemDtoToCreate, 1L);
        Booking createdBooking = bookingService.createBooking(bookingToCreate, 2L, 1L);

        List<Booking> userBookingsList = bookingService.getAllUserBookings(2L, State.ALL, 0, 10);

        assertThat(userBookingsList, hasSize(1));
        assertThat(userBookingsList.get(0).getId(), equalTo(createdBooking.getId()));
    }

    @Test
    void getAllUserItemsBookings() {
        userService.createUser(ownerOfItem);
        userService.createUser(booker);
        itemService.createItem(itemDtoToCreate, 1L);
        Booking createdBooking = bookingService.createBooking(bookingToCreate, 2L, 1L);

        List<Booking> userBookingsList = bookingService.getAllUserItemsBookings(1L, State.ALL, 0, 10);

        assertThat(userBookingsList, hasSize(1));
        assertThat(userBookingsList.get(0).getId(), equalTo(createdBooking.getId()));
    }

}
