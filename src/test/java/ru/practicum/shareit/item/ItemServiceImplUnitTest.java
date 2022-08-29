package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestService requestService;

    User user = new User(1L, "testUser", "test@email.com");

    User user2 = new User(2L, "testUser2", "test2@email.com");

    Item item = Item.builder()
            .id(1L)
            .name("testItem")
            .description("testDescr")
            .available(true)
            .owner(user)
            .build();

    Comment comment = Comment.builder()
            .id(1L)
            .text("testText")
            .item(item)
            .author(user)
            .build();

    ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("testItem")
            .description("testDescr")
            .available(true)
            .requestId(1L)
            .build();

    CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("testText")
            .item(itemDto)
            .authorName("testName")
            .build();

    Booking lastBooking = Booking.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .build();

    Booking nextBooking = Booking.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .build();

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository,
                commentRepository, requestService);
    }

    @Test
    public void createItem_shouldReturnItemWhenCreateItem() {
        when(userService.getUserById(anyLong())).thenReturn(user);

        when(itemRepository.save(any())).thenReturn(item);

        ItemDto createdItemDto = itemService.createItem(itemDto, 1L);

        assertThat(createdItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(createdItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(createdItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    public void createItem_shouldThrowNotFoundExceptionWhenUserNotFoundInRepository() {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User is not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
        assertThat(e.getMessage(), equalTo("User is not found"));
    }

    @Test
    public void createItem_shouldThrowNotFoundExceptionWhenRequestNotFoundInRepository() {
        when(userService.getUserById(anyLong())).thenReturn(user);

        when(requestService.getRequestById(anyLong(), anyLong())).thenThrow(new NotFoundException("Request is not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
        assertThat(e.getMessage(), equalTo("Request is not found"));
    }

    @Test
    public void getItemById_shouldReturnItemDtoWhenOwnerRequestItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        when(bookingRepository.getLastItemBooking(anyLong(), any())).thenReturn(Optional.of(lastBooking));

        when(bookingRepository.getNextItemBooking(anyLong(), any())).thenReturn(Optional.of(nextBooking));

        var requestedItemDto = itemService.getItemById(1L, 1L);

        assertThat(requestedItemDto.getName(), equalTo(item.getName()));
        assertThat(requestedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(requestedItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(requestedItemDto.getComments(), hasSize(1));
        assertThat(requestedItemDto.getLastBooking().getId(), equalTo(1L));
        assertThat(requestedItemDto.getLastBooking().getBookerId(), equalTo(1L));
        assertThat(requestedItemDto.getNextBooking().getId(), equalTo(2L));
        assertThat(requestedItemDto.getNextBooking().getBookerId(), equalTo(1L));
    }

    @Test
    public void getItemById_shouldReturnItemDtoWhenNotOwnerRequestItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        var requestedItemDto = itemService.getItemById(1L, 2L);

        assertThat(requestedItemDto.getName(), equalTo(item.getName()));
        assertThat(requestedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(requestedItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(requestedItemDto.getComments(), hasSize(1));
        assertThat(requestedItemDto.getLastBooking(), equalTo(null));
        assertThat(requestedItemDto.getNextBooking(), equalTo(null));
    }

    @Test
    void getAllByUserItems_shouldReturnItem() {
        when(itemRepository.getAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));
        when(bookingRepository.getLastItemBooking(anyLong(), any())).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.getNextItemBooking(anyLong(), any())).thenReturn(Optional.of(nextBooking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        var userItemsList = itemService.getAllUserItems(1L, 0, 10);

        assertThat(userItemsList, hasSize(1));
        assertThat(userItemsList.get(0).getLastBooking().getId(), equalTo(1L));
        assertThat(userItemsList.get(0).getLastBooking().getBookerId(), equalTo(1L));
        assertThat(userItemsList.get(0).getNextBooking().getId(), equalTo(2L));
        assertThat(userItemsList.get(0).getNextBooking().getBookerId(), equalTo(1L));
        assertThat(userItemsList.get(0).getComments(), hasSize(1));
    }
}