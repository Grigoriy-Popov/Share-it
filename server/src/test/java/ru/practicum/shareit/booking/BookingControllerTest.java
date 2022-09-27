package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH_BOOKINGS = "/bookings";

    User ownerOfItem = new User(null, "testUser", "test@email.com");
    User booker = new User(null, "testUser2", "test2@email.com");
    Item item = Item.builder().name("testItem").description("testDescription").available(true)
            .owner(ownerOfItem).build();
    Booking booking = Booking.builder().id(1L)
            .start(LocalDateTime.of(2222, 12, 12, 12, 12, 12))
            .end(LocalDateTime.of(2223, 12, 12, 12, 12, 12))
            .booker(booker).item(item).status(BookingStatus.WAITING).build();
    InputBookingDto inputBookingDto = InputBookingDto.builder()
            .start(LocalDateTime.of(2222, 12, 12, 12, 12, 12))
            .end(LocalDateTime.of(2223, 12, 12, 12, 12, 12))
            .itemId(1L).build();
    InputBookingDto invalidInputBookingDtoWithWrongStart = InputBookingDto.builder()
            .start(LocalDateTime.of(1111, 12, 12, 12, 12, 12))
            .end(LocalDateTime.of(2223, 12, 12, 12, 12, 12))
            .itemId(1L).build();
    OutputBookingDto outputBookingDto = BookingMapper.toOutputDto(booking);

    @Test
    void createValidBooking() throws Exception {
        when(bookingService.createBooking(any(), anyLong(), anyLong()))
                .thenReturn(booking);
        mvc.perform(post(BASE_PATH_BOOKINGS)
                        .header(USER_ID_HEADER, 2L)
                        .content(mapper.writeValueAsString(inputBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(outputBookingDto)));
    }

//    @Test
//    void createBookingWithWrongStart_shouldReturnStatus400() throws Exception {
//        when(bookingService.createBooking(any(), anyLong(), anyLong()))
//                .thenReturn(booking);
//        mvc.perform(post(BASE_PATH_BOOKINGS)
//                        .header(USER_ID_HEADER, 2L)
//                        .content(mapper.writeValueAsString(invalidInputBookingDtoWithWrongStart))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch(BASE_PATH_BOOKINGS + "/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(outputBookingDto)));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get(BASE_PATH_BOOKINGS + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(outputBookingDto)));
    }

//    @Test
//    void getAllUserBookings() throws Exception {
//        when(bookingService.getAllUserBookings(anyLong(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(booking));
//
//        mvc.perform(get(BASE_PATH_BOOKINGS + "?state=ALL")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(USER_ID_HEADER, 2L)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().json(mapper.writeValueAsString(List.of(outputBookingDto))));
//    }

//    @Test
//    void getAllUserItemsBookings() throws Exception {
//        when(bookingService.getAllUserItemsBookings(anyLong(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(booking));
//
//        mvc.perform(get(BASE_PATH_BOOKINGS + "/owner?state=ALL")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(USER_ID_HEADER, 1L)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().json(mapper.writeValueAsString(List.of(outputBookingDto))));
//    }

    @Test
    void getAllUserBookings_shouldReturnStatus400() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get(BASE_PATH_BOOKINGS + "/owner?state=wrongState")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
