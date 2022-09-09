package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplUnitTest {
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserService userService;

    ItemRequest itemRequest = ItemRequest.builder().id(1L).description("testDescr").build();

    @Test
    void createRequest_shouldThrowExceptionWhenUserNotFound() {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.createRequest(itemRequest, 1L));
        assertThat(e.getMessage(), equalTo("User not found"));
    }

    @Test
    void getAllUserRequests_shouldThrowExceptionWhenUserNotFound() {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllUserRequests(1L));
        assertThat(e.getMessage(), equalTo("User not found"));
    }

    @Test
    void getAllRequests_shouldThrowExceptionWhenUserNotFound() {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getAllRequests(1L, 0, 10));
        assertThat(e.getMessage(), equalTo("User not found"));
    }

    @Test
    void getRequestById_shouldThrowExceptionWhenUserNotFound() {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));
        assertThat(e.getMessage(), equalTo("User not found"));
    }

    @Test
    void getRequestById_shouldThrowExceptionWhenRequestNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));
        assertThat(e.getMessage(), equalTo("Request with id 1 not found"));
    }
}