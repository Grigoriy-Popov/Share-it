package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserHasNotBookedItem;
import ru.practicum.shareit.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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

    User user = new User(1L, "testUser", "test@email.com");
    Item item = Item.builder().id(1L).name("testItem").description("testDescr").available(true).owner(user).build();
    Comment comment = Comment.builder().id(1L).text("testText").item(item).author(user).build();
    ItemDto itemDto = ItemDto.builder().id(1L).name("testItem").description("testDescr").available(true)
            .requestId(1L).build();
    Booking lastBooking = Booking.builder().id(1L).item(item).booker(user).build();
    Booking nextBooking = Booking.builder().id(2L).item(item).booker(user).build();

    @Test
    public void createItem_shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User is not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
        assertThat(e.getMessage(), equalTo("User is not found"));
    }

    @Test
    public void getItemById_shouldReturnItemDtoWithBookingsWhenOwnerRequestItem() {
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
    public void getItemById_shouldReturnItemDtoWithoutBookingsWhenNotOwnerRequestItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        var requestedItemDto = itemService.getItemById(1L, 2L);

        assertThat(requestedItemDto.getName(), equalTo(item.getName()));
        assertThat(requestedItemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(requestedItemDto.getAvailable(), equalTo(item.getAvailable()));
        assertThat(requestedItemDto.getComments(), hasSize(1));
        assertThat(requestedItemDto.getLastBooking(), nullValue());
        assertThat(requestedItemDto.getNextBooking(), nullValue());
    }

    @Test
    public void getItemById_shouldThrowNotFoundExceptionWhenItemIsNotExist() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> itemService
                .getItemById(1L, 1L));
        assertThat(e.getMessage(), equalTo("Item with id 1 not found"));
    }

    @Test
    void getAllUserItems_shouldReturnItem() {
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

    @Test
    public void editItem_shouldThrowNotFoundExceptionWhenItemIsNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.editItem(itemDto, 1L, 1L));
        assertThat(e.getMessage(), equalTo("Item with id 1 not found"));
    }

    @Test
    public void editItem_shouldThrowUserIsNotOwnerExceptionWhenNotOwnerRequestUpdate() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Exception e = Assertions.assertThrows(UserIsNotOwnerException.class,
                () -> itemService.editItem(itemDto, 1L, 2L));
        assertThat(e.getMessage(), equalTo("User with id 2 is not the owner of the item"));
    }

    @Test
    public void searchAvailableItemsByKeyword_shouldReturnEmptyListWhenKeywordIsEmpty() {
        var itemsList = itemService.searchAvailableItemsByKeyword("", 0, 10);

        assertThat(itemsList, hasSize(0));
    }

    @Test
    public void addComment_shouldThrowExceptionWhenUserNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userService.getUserById(anyLong())).thenThrow(new NotFoundException("User is not found"));

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> itemService.addComment(comment,
                1L, 1L));
        assertThat(e.getMessage(), equalTo("User is not found"));
    }

    @Test
    void addComment_shouldThrowExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> itemService.addComment(comment,
                1L, 1L));
        assertThat(e.getMessage(), equalTo("Item with id 1 not found"));
    }

    @Test
    public void addComment_shouldThrowExceptionWhenUserNotFinishedAtLeastOneBookingOfItem() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(new ArrayList<>());

        Exception e = Assertions.assertThrows(UserHasNotBookedItem.class, () -> itemService.addComment(comment,
                1L, 1L));

        assertThat(e.getMessage(), equalTo("You need to finish at least one booking to leave a comment"));
    }
}